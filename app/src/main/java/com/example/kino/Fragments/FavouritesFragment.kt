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
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.ApiKey
import com.example.kino.RecyclerViewAdapter
import com.example.kino.MovieClasses.*
import com.example.kino.R
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouritesFragment: Fragment(), RecyclerViewAdapter.RecyclerViewItemClick {
    

    private lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var sessionId: String= ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favourites_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE)

        if(sharedPreferences.contains(getString(R.string.session_id))) {
            sessionId = sharedPreferences.getString(getString(R.string.session_id), "null") as String
        }

        bindViews(view)

        recyclerViewAdapter = this.context?.let { RecyclerViewAdapter(it, itemClickListener = this) }
        recyclerView.adapter = recyclerViewAdapter

        getMovies()

    }

    private fun bindViews(view: View) = with(view) {
        recyclerView=view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager=LinearLayoutManager(context)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies()
        }
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.id)
        startActivity(intent)
    }

   private fun getMovies(){

        RetrofitService.getPostApi().getFavouriteMovies(ApiKey, sessionId).enqueue(object : Callback<Movies> {
            override fun onFailure(call: Call<Movies>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                if(response.isSuccessful){
                    val movies: Movies? = response.body()
                    if(movies?.movieList?.size==0){
                        swipeRefreshLayout.isRefreshing=false
                    }
                    else {
                        recyclerViewAdapter?.movies = movies?.movieList
                        for(movie in recyclerViewAdapter?.movies!!){
                            movie.genreNames = mutableListOf()
                            for(genre_id in movie.genres) {
                                GenresList.genres?.get(genre_id)?.let { movie.genreNames.add(it) }
                            }
                            movie.isClicked = true
                        }
                        recyclerViewAdapter?.notifyDataSetChanged()
                        swipeRefreshLayout.isRefreshing=false
                    }
                }
            }
        })
    }

    override fun addToFavourites(position: Int, item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isClicked){
            item.isClicked=true
            selectedMovie=SelectedMovie("movie",item.id, item.isClicked)
            RetrofitService.getPostApi().addRemoveFavourites(ApiKey,sessionId,selectedMovie).enqueue(object: Callback<StatusResponse>{
                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}

                override fun onResponse(
                    call: Call<StatusResponse>,
                    response: Response<StatusResponse>
                ) {}
            })
        }
        else{
            item.isClicked=false
            selectedMovie=SelectedMovie("movie",item.id, item.isClicked)
            selectedMovie.selectedStatus=item.isClicked
            RetrofitService.getPostApi().addRemoveFavourites(ApiKey, sessionId, selectedMovie).enqueue(object: Callback<StatusResponse>{
                override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}

                override fun onResponse(
                    call: Call<StatusResponse>,
                    response: Response<StatusResponse>
                ) {}
            })
        }
    }
}