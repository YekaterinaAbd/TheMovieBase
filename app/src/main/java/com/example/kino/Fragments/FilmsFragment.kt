package com.example.kino.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.MovieAdapter
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.ApiKey
import com.example.kino.MovieClasses.*
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmsFragment: Fragment(), MovieAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var movieAdapter: MovieAdapter? = null
    private lateinit var getSharedPref: SharedPreferences
    private lateinit var sessionId:String
    lateinit var processedMovies:MutableList<Movie>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)!!

        if(getSharedPref.contains(getString(R.string.session_id))){
            sessionId=getSharedPref.getString(getString(R.string.session_id),"null") as String
        }

        GenresList.getGenres()

        recyclerView = view.findViewById(R.id.classicRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            movieAdapter?.clearAll()
            getPosts()
        }

        movieAdapter = this.context?.let { MovieAdapter(it, itemClickListener = this) }
        recyclerView.adapter = movieAdapter
        getPosts()
    }


    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.id)
        startActivity(intent)
    }


    override fun addToFavouritesClick(position: Int, item: Movie) {
        lateinit var favouritesRequest: FavouritesRequest

        if (!item.isClicked){
            item.isClicked=true
            favouritesRequest=FavouritesRequest("movie",item.id, item.isClicked)
            RetrofitService.getPostApi().addRemoveFavourites(ApiKey,sessionId,favouritesRequest).enqueue(object: Callback<FavouritesResponse>{
                override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {}

                override fun onResponse(
                    call: Call<FavouritesResponse>,
                    response: Response<FavouritesResponse>
                ) {}
            })
        }
        else{
            item.isClicked=false
            favouritesRequest=FavouritesRequest("movie",item.id, item.isClicked)
            favouritesRequest.favourite=item.isClicked
            RetrofitService.getPostApi().addRemoveFavourites(ApiKey, sessionId, favouritesRequest).enqueue(object: Callback<FavouritesResponse>{
                override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {}

                override fun onResponse(
                    call: Call<FavouritesResponse>,
                    response: Response<FavouritesResponse>
                ) {}
            })
        }
    }

      private fun getPosts() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getPostApi().getMovieList(ApiKey).enqueue(object : Callback<MovieResults> {
            override fun onFailure(call: Call<MovieResults>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(call: Call<MovieResults>, response: Response<MovieResults>) {
                processedMovies= mutableListOf()
                if (response.isSuccessful) {
                    val movies = response.body()
                    for(movie: Movie in movies!!.results ){
                        getMovieStats(movie)
                        processedMovies.add(movie)
                    }
                    movieAdapter?.movies = processedMovies
                    for(movie in movieAdapter?.movies!!){
                        movie.genreNames = mutableListOf()
                        for(genre_id in movie.genres) {
                            GenresList.genres?.get(genre_id)?.let {
                                movie.genreNames.add(it)
                            }
                        }
                    }
                    movieAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    fun getMovieStats(movie:Movie){
        RetrofitService.getPostApi().getMovieStates(movie.id,ApiKey,sessionId).enqueue(object: Callback<MovieStatsResponse>{
            override fun onFailure(call: Call<MovieStatsResponse>, t: Throwable) {}
            override fun onResponse(call: Call<MovieStatsResponse>, response: Response<MovieStatsResponse>) {
                if(response.isSuccessful){
                    val movieStatsResponse=response.body()
                    movie.isClicked = movieStatsResponse!!.favourite
                    movieAdapter?.notifyDataSetChanged()
                }
            }
        })
    }
}