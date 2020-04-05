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
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.MovieAdapter
import com.example.kino.MovieClasses.*
import com.example.kino.R
import com.example.kino.RetrofitService
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouritesFragment: Fragment(), MovieAdapter.RecyclerViewItemClick {

    lateinit var recyclerView: RecyclerView
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val API_KEY: String = "d118a5a4e56930c8ce9bd2321609d877"
    var movieAdapter: MovieAdapter? = null
    var session_id: String= "";
    lateinit var getSharedPref: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var view: View =inflater.inflate(R.layout.favourites_fragment, container, false)
        recyclerView=view.findViewById(R.id.favRecyclerView)

        recyclerView.layoutManager=LinearLayoutManager(context);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            movieAdapter?.clearAll()
            getPosts()
        }
        movieAdapter = this.context?.let { MovieAdapter(it, itemClickListener = this) }
        recyclerView.adapter = movieAdapter
        getPosts()
        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.id)
        startActivity(intent)
    }


   private fun getPosts(){

        getSharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )!!
        if(getSharedPref.contains(getString(R.string.session_id))) {
            session_id = getSharedPref.getString(getString(R.string.session_id), "null")
        }
        RetrofitService.getPostApi().getFavouriteMovies(API_KEY, session_id).enqueue(object : Callback<MovieResults> {
            override fun onFailure(call: Call<MovieResults>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }
            override fun onResponse(call: Call<MovieResults>, response: Response<MovieResults>) {
                if(response.isSuccessful){
                    val movies: MovieResults? = response.body();
                    if(movies?.results?.size==0){
                        Log.d("sizeZero","Correct")
                        swipeRefreshLayout.isRefreshing=false;
                    }
                    else {
                        movieAdapter?.movies = movies?.results;
                        for(movie in movieAdapter?.movies!!){
                            //movie.isClicked = true
                            movie.genre_names = mutableListOf()
                            for(genre_id in movie.genres) {
                                GenresList.genres?.get(genre_id)?.let { movie.genre_names.add(it) }
                            }
                            movie.isClicked = true
                        }
                        movieAdapter?.notifyDataSetChanged()
                        swipeRefreshLayout.isRefreshing=false;
                    }
                }
            }
        })
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
}