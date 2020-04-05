package com.example.kino.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.MovieAdapter
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.MovieClasses.*
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmsFragment: Fragment(),
    MovieAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val API_KEY: String = "d118a5a4e56930c8ce9bd2321609d877"
    private var movieAdapter: MovieAdapter? = null
    lateinit var getSharedPref: SharedPreferences
    lateinit var session_id:String;
    lateinit var processedMovies:MutableList<Movie>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )!!
        if(getSharedPref.contains(getString(R.string.session_id))){
            session_id=getSharedPref.getString(getString(R.string.session_id),"null");
            Log.d("Pref","Correct")
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
        getSharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )!!
        if(getSharedPref.contains(getString(R.string.session_id))){
            session_id=getSharedPref.getString(getString(R.string.session_id),"null");
            Log.d("Pref","Correct")
        }
        if (!item.isClicked){
            Log.d("Created","Correct")
            item.isClicked=true;
            favouritesRequest=FavouritesRequest("movie",item.id, item.isClicked)
            RetrofitService.getPostApi().addRemoveFavourites(API_KEY,session_id,favouritesRequest).enqueue(object: Callback<FavouritesResponse>{
                override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<FavouritesResponse>,
                    response: Response<FavouritesResponse>
                ) {

                }
            })

        }
        else{
            item.isClicked=false;
            favouritesRequest=FavouritesRequest("movie",item.id, item.isClicked)
            favouritesRequest.favourite=item.isClicked;
            RetrofitService.getPostApi().addRemoveFavourites(API_KEY, session_id, favouritesRequest).enqueue(object: Callback<FavouritesResponse>{
                override fun onFailure(call: Call<FavouritesResponse>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<FavouritesResponse>,
                    response: Response<FavouritesResponse>
                ) {

                }

            })
        }
    }


      fun getPosts() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getPostApi().getMovieList(API_KEY).enqueue(object : Callback<MovieResults> {
            override fun onFailure(call: Call<MovieResults>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(call: Call<MovieResults>, response: Response<MovieResults>) {
                processedMovies= mutableListOf();
                if (response.isSuccessful) {
                    val movies = response.body()
                    for(movie: Movie in movies!!.results ){
                        getMovieStats(movie)
                        processedMovies.add(movie)
                    }
                    movieAdapter?.movies = processedMovies
                    for(movie in movieAdapter?.movies!!){
                        movie.genre_names = mutableListOf()
                        for(genre_id in movie.genres) {
                            GenresList.genres?.get(genre_id)?.let {
                                movie.genre_names.add(it)
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
        getSharedPref= activity?.getSharedPreferences( getString(R.string.preference_file), Context.MODE_PRIVATE)!!
        if(getSharedPref.contains(getString(R.string.session_id))) {
            session_id = getSharedPref.getString(getString(R.string.session_id), "null")
        }
        RetrofitService.getPostApi().getMovieStates(movie.id,API_KEY,session_id).enqueue(object: Callback<MovieStatsResponse>{
            override fun onFailure(call: Call<MovieStatsResponse>, t: Throwable) {
            }
            override fun onResponse(call: Call<MovieStatsResponse>, response: Response<MovieStatsResponse>) {
                if(response.isSuccessful){
                    val movieStatsResponse=response.body()
                    movie.isClicked = movieStatsResponse!!.favourite
                    movieAdapter?.notifyDataSetChanged();
                    Log.d("movieM", "Working")
                }
            }

        })
    }
}