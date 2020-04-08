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
import com.example.kino.MovieClasses.*
import com.example.kino.R
import com.example.kino.RecyclerViewAdapter
import com.example.kino.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilmsFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String
    private lateinit var processedMovies: MutableList<Movie>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        if (sharedPref.contains(getString(R.string.session_id))) {
            sessionId = sharedPref.getString(getString(R.string.session_id), "null") as String
        }

        GenresList.getGenres()

        recyclerView = view.findViewById(R.id.filmsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies()
        }

        recyclerViewAdapter =
            this.context?.let { RecyclerViewAdapter(itemClickListener = this) }
        recyclerView.adapter = recyclerViewAdapter
        getMovies()
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.id)
        startActivity(intent)
    }


    private fun getMovies() {
        swipeRefreshLayout.isRefreshing = true
        RetrofitService.getPostApi().getMovieList(ApiKey).enqueue(object : Callback<Movies> {
            override fun onFailure(call: Call<Movies>, t: Throwable) {
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onResponse(call: Call<Movies>, response: Response<Movies>) {
                processedMovies = mutableListOf()

                if (response.isSuccessful) {
                    val movies = response.body()
                    if (movies != null) {
                        for (movie: Movie in movies.movieList) {
                            likeStatusSaver(movie)
                            processedMovies.add(movie)
                        }
                    }
                    recyclerViewAdapter?.movies = processedMovies
                    if (recyclerViewAdapter?.movies != null) {
                        for (movie in recyclerViewAdapter?.movies as MutableList<Movie>) {
                            movie.genreNames = mutableListOf()
                            for (genreId in movie.genres) {
                                GenresList.genres?.get(genreId)?.let {
                                    movie.genreNames.add(it)
                                }
                            }
                        }
                    }
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
                swipeRefreshLayout.isRefreshing = false
            }
        })
    }

    override fun addToFavourites(position: Int, item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isClicked) {
            item.isClicked = true
            selectedMovie = SelectedMovie("movie", item.id, item.isClicked)

            RetrofitService.getPostApi().addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                .enqueue(object : Callback<StatusResponse> {
                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}

                    override fun onResponse(
                        call: Call<StatusResponse>,
                        response: Response<StatusResponse>
                    ) {
                    }
                })
        } else {
            item.isClicked = false
            selectedMovie = SelectedMovie("movie", item.id, item.isClicked)

            RetrofitService.getPostApi().addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                .enqueue(object : Callback<StatusResponse> {
                    override fun onFailure(call: Call<StatusResponse>, t: Throwable) {}
                    override fun onResponse(
                        call: Call<StatusResponse>,
                        response: Response<StatusResponse>
                    ) {
                    }
                })
        }
    }

    fun likeStatusSaver(movie: Movie) {

        RetrofitService.getPostApi().getMovieStates(movie.id, ApiKey, sessionId)
            .enqueue(object : Callback<MovieStatus> {
                override fun onFailure(call: Call<MovieStatus>, t: Throwable) {}
                override fun onResponse(call: Call<MovieStatus>, response: Response<MovieStatus>) {
                    if (response.isSuccessful) {
                        val movieStatus = response.body()
                        if (movieStatus != null) {
                            movie.isClicked = movieStatus.selectedStatus
                            recyclerViewAdapter?.notifyDataSetChanged()
                        }
                    }
                }
            })
    }


}