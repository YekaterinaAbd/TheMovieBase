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
import com.example.kino.*
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.MovieClasses.GenresList
import com.example.kino.MovieClasses.Movie
import com.example.kino.MovieClasses.MovieStatus
import com.example.kino.MovieClasses.SelectedMovie
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class FilmsFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick, CoroutineScope {

    private var movieDao: MovieDao? = null
    private var movieStatusDao: MovieStatusDao? = null

    private val defaultValue: String = "default"
    private val intentKey: String = "movie_id"
    private val mediaType: String = "movie"

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sessionId: String

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieDao = MovieDatabase.getDatabase(context = requireActivity()).movieDao()
        movieStatusDao = MovieDatabase.getDatabase(context = requireActivity()).movieStatusDao()

        getSharedPreferences()
        GenresList.getGenres()
        bindViews(view)
        setAdapter()
        getMovies()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun getSharedPreferences() {
        sharedPref = requireActivity().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        if (sharedPref.contains(getString(R.string.session_id))) {
            sessionId = sharedPref.getString(getString(R.string.session_id), defaultValue) as String
        }
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.filmsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies()
        }
    }

    private fun setAdapter() {
        recyclerViewAdapter = RecyclerViewAdapter(itemClickListener = this)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra(intentKey, item.id)
        startActivity(intent)
    }

    private fun getMovies() {
        launch {
            swipeRefreshLayout.isRefreshing = true

            val moviesList = withContext(Dispatchers.IO) {
                try {
                    updateFavourites()
                    val response = RetrofitService.getPostApi().getMovieList(ApiKey)
                    if (response.isSuccessful) {

                        val movies = response.body()?.movieList
                        if (movies != null) {
                            for (movie: Movie in movies) {
                                saveLikeStatus(movie)
                            }
                        }
                        if (!movies.isNullOrEmpty()) {
                            for (movie in movies) {
                                setMovieGenres(movie)
                            }
                            movieDao?.deleteAll()
                            movieDao?.insertAll(movies)
                        }
                        return@withContext movies
                    } else {
                        return@withContext movieDao?.getMovies() ?: emptyList()
                    }

                } catch (e: Exception) {
                    return@withContext movieDao?.getMovies() ?: emptyList()
                }
            }
            swipeRefreshLayout.isRefreshing = false
            if (moviesList != null) {
                recyclerViewAdapter?.replaceItems(moviesList)
            }
        }
    }

    private fun setMovieGenres(movie: Movie) {
        movie.genreNames = ""
        movie.genreIds?.forEach { genreId ->
            val genreName = GenresList.genres?.get(genreId)
                .toString().toLowerCase(Locale.ROOT)
            movie.genreNames += getString(R.string.genre_name, genreName)
        }
    }

    private fun updateFavourites() {
        val moviesToUpdate = movieStatusDao?.getMovieStatuses()
        if (!moviesToUpdate.isNullOrEmpty()) {
            for (movie in moviesToUpdate) {
                val selectedMovie = SelectedMovie(
                    movieId = movie.movieId,
                    selectedStatus = movie.selectedStatus
                )
                addRemoveFavourites(selectedMovie)
            }
        }
        movieStatusDao?.deleteAll()
    }

    override fun addToFavourites(position: Int, item: Movie) {
        lateinit var selectedMovie: SelectedMovie

        if (!item.isClicked) {
            item.isClicked = true
            selectedMovie = SelectedMovie(mediaType, item.id, item.isClicked)
        } else {
            item.isClicked = false
            selectedMovie = SelectedMovie(mediaType, item.id, item.isClicked)
        }
        addRemoveFavourites(selectedMovie)
    }

    private fun addRemoveFavourites(selectedMovie: SelectedMovie) {
        launch {
            try {
                val response = RetrofitService.getPostApi()
                    .addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                if (response.isSuccessful) {
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    movieDao?.updateMovieIsCLicked(
                        selectedMovie.selectedStatus,
                        selectedMovie.movieId
                    )
                    val movieStatus =
                        MovieStatus(selectedMovie.movieId, selectedMovie.selectedStatus)
                    movieStatusDao?.insertMovieStatus(movieStatus)
                }
            }
        }
    }

    private fun saveLikeStatus(movie: Movie) {
        launch {
            try {
                val response =
                    RetrofitService.getPostApi().getMovieStates(movie.id, ApiKey, sessionId)
                if (response.isSuccessful) {
                    val movieStatus = response.body()
                    if (movieStatus != null) {
                        movie.isClicked = movieStatus.selectedStatus
                        withContext(Dispatchers.IO) {
                            movieDao?.updateMovieIsCLicked(movie.isClicked, movie.id)
                        }
                        recyclerViewAdapter?.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}

