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

class FavouritesFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick, CoroutineScope {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var sessionId: String = ""
    private lateinit var sharedPreferences: SharedPreferences

    private var movieDao: MovieDao? = null
    private var movieStatusDao: MovieStatusDao? = null

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favourites_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieDao = MovieDatabase.getDatabase(context = requireActivity()).movieDao()
        movieStatusDao = MovieDatabase.getDatabase(context = requireActivity()).movieStatusDao()

        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.preference_file), Context.MODE_PRIVATE
        )

        if (sharedPreferences.contains(getString(R.string.session_id))) {
            sessionId =
                sharedPreferences.getString(getString(R.string.session_id), "null") as String
        }

        bindViews(view)

        recyclerViewAdapter =
            this.context?.let { RecyclerViewAdapter(itemClickListener = this) }
        recyclerView.adapter = recyclerViewAdapter

        getMovies()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
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

    private fun getMovies() {
        launch {
            val favouriteMoviesList = withContext(Dispatchers.IO) {
                try {

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

                    val response =
                        RetrofitService.getPostApi().getFavouriteMovies(ApiKey, sessionId)
                    if (response.isSuccessful) {
                        val movies = response.body()?.movieList
                        if (!movies.isNullOrEmpty()) {

                            for (movie in movies) {
                                movie.genreNames = ""
                                if (movie.genreIds != null) {
                                    for (genreId in movie.genreIds!!) {
                                        movie.genreNames += GenresList.genres?.get(genreId)
                                            .toString().toLowerCase(Locale.ROOT) + ", "
                                    }
                                }
                                movie.isClicked = true
                            }
                        }
                        return@withContext movies
                    } else {
                        return@withContext movieDao?.getFavouriteMovies() ?: emptyList()
                    }
                } catch (e: Exception) {
                    return@withContext movieDao?.getFavouriteMovies() ?: emptyList()
                }

            }
            swipeRefreshLayout.isRefreshing = false
            recyclerViewAdapter?.movies = favouriteMoviesList
            recyclerViewAdapter?.notifyDataSetChanged()

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
        addRemoveFavourites(selectedMovie)
    }

    private fun addRemoveFavourites(selectedMovie: SelectedMovie) {
        launch {
            try {
                val response = RetrofitService.getPostApi()
                    .addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                if (response.isSuccessful) {
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    val movieStatus =
                        MovieStatus(selectedMovie.movieId, selectedMovie.selectedStatus)
                    movieStatusDao?.insertMovieStatus(movieStatus)
                    movieDao?.updateMovieIsCLicked(
                        selectedMovie.selectedStatus,
                        selectedMovie.movieId
                    )
                }
                recyclerViewAdapter?.notifyDataSetChanged()
            }
            recyclerViewAdapter?.notifyDataSetChanged()
        }
    }
}
