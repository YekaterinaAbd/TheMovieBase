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
import com.example.kino.*
import com.example.kino.Activities.MovieDetailActivity
import com.example.kino.MovieClasses.GenresList
import com.example.kino.MovieClasses.Movie
import com.example.kino.MovieClasses.Movies
import com.example.kino.MovieClasses.SelectedMovie
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class FavouritesFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick, CoroutineScope {

    private val job = Job()

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var sessionId: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var movieDao: MovieDao? =null;
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
            try {
                val response = RetrofitService.getPostApi().getFavouriteMovies(ApiKey, sessionId)
                if (response.isSuccessful) {
                    val movies: Movies? = response.body()
                    if (movies?.movieList?.size == 0) {
                        swipeRefreshLayout.isRefreshing = false
                    } else {
                        recyclerViewAdapter?.movies = movies?.movieList
                        if (recyclerViewAdapter?.movies != null) {
                            for (movie in recyclerViewAdapter?.movies as MutableList<Movie>) {
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
                        recyclerViewAdapter?.notifyDataSetChanged()
                    }
                }
                else{
                    withContext(Dispatchers.IO){
                        recyclerViewAdapter?.movies=movieDao?.getFavouritesMovies()
                    }
                }
                swipeRefreshLayout.isRefreshing = false
            } catch (e: Exception) {
                withContext(Dispatchers.IO){
                    recyclerViewAdapter?.movies=movieDao?.getFavouritesMovies()
                }
                recyclerViewAdapter?.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
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
            selectedMovie.selectedStatus = item.isClicked
        }
        launch {
            try {
                val response =
                    RetrofitService.getPostApi()
                        .addRemoveFavourites(ApiKey, sessionId, selectedMovie)
                if (response.isSuccessful) {

                }
            } catch (e: Exception) {
                Toast.makeText(context, "No Internet connection", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
