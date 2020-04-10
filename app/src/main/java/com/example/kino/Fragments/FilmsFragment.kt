package com.example.kino.Fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.ApiKey
import com.example.kino.MovieClasses.GenresList
import com.example.kino.MovieClasses.Movie
import com.example.kino.MovieClasses.SelectedMovie
import com.example.kino.R
import com.example.kino.RecyclerViewAdapter
import com.example.kino.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class FilmsFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick, CoroutineScope {

    private val job = Job()

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String
    private lateinit var processedMovies: MutableList<Movie>

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra("movie_id", item.id)
        startActivity(intent)
    }


    private fun getMovies() {
        launch {
            swipeRefreshLayout.isRefreshing = true
            try {
                val response = RetrofitService.getPostApi().getMovieList(ApiKey)
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

            } catch (e: Exception) {
                swipeRefreshLayout.isRefreshing = false

                //get data from database
            }
        }
    }

    override fun addToFavourites(position: Int, item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isClicked) {
            item.isClicked = true
            selectedMovie = SelectedMovie("movie", item.id, item.isClicked)

        } else {
            item.isClicked = false
            selectedMovie = SelectedMovie("movie", item.id, item.isClicked)

        }
        launch {
            try {
                val response = RetrofitService.getPostApi()
                    .addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                if (response.isSuccessful) {
                }
            } catch (e: Exception) {
                Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun likeStatusSaver(movie: Movie) {
        launch {
            try {
                val response =
                    RetrofitService.getPostApi().getMovieStates(movie.id, ApiKey, sessionId)
                if (response.isSuccessful) {
                    val movieStatus = response.body()
                    if (movieStatus != null) {
                        movie.isClicked = movieStatus.selectedStatus
                        recyclerViewAdapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
            }
        }

    }

}