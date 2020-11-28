package com.example.kino.presentation.ui.lists.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.example.kino.presentation.utils.constants.MOVIE_CLICKED
import com.example.kino.presentation.utils.constants.MOVIE_ID
import com.example.kino.presentation.utils.constants.MOVIE_TITLE
import com.example.kino.presentation.utils.widgets.MoviesListView
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

class MainListsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var topMoviesView: MoviesListView
    private lateinit var currentMoviesView: MoviesListView
    private lateinit var upcomingMoviesView: MoviesListView

    private val moviesListViewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : HorizontalFilmsAdapter.ItemClickListener {
        override fun itemClick(position: Int, item: Movie) {
            logEvent(MOVIE_CLICKED, item)
            val bundle = Bundle()
            bundle.putInt(INTENT_KEY, item.id)

            val movieDetailedFragment = MovieDetailsFragment()
            movieDetailedFragment.arguments = bundle

            parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
                .addToBackStack(this@MainListsFragment.toString()).commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        bindViews(view)
        getMovies()
        observe()
    }

    private fun bindViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        topMoviesView = view.findViewById(R.id.topMovies)
        currentMoviesView = view.findViewById(R.id.currentMovies)
        upcomingMoviesView = view.findViewById(R.id.upcomingMovies)

        topMoviesView.apply {
            setData(MoviesType.TOP)
            setListeners(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        currentMoviesView.apply {
            setData(MoviesType.CURRENT)
            setListeners(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        upcomingMoviesView.apply {
            setData(MoviesType.UPCOMING)
            setListeners(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        swipeRefreshLayout.setOnRefreshListener {
            clear()
            getMovies()
        }
    }

    private fun logEvent(logMessage: String, item: Movie) {
        val bundle = Bundle()
        bundle.putString(MOVIE_ID, item.id.toString())
        bundle.putString(MOVIE_TITLE, item.title)
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun getMovies() {
        getMovies(MoviesType.TOP)
        getMovies(MoviesType.CURRENT)
        getMovies(MoviesType.UPCOMING)
    }

    private fun getMovies(type: MoviesType) {
        moviesListViewModel.getMovies(type, 1)
    }

    private fun clear() {
        topMoviesView.clearAdapter()
        currentMoviesView.clearAdapter()
        upcomingMoviesView.clearAdapter()
    }

    private fun observe() {
        moviesListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    if (result.type == MoviesType.TOP) {
                        addToAdapter(topMoviesView, result.moviesList)
                    }
                    if (result.type == MoviesType.CURRENT) {
                        addToAdapter(currentMoviesView, result.moviesList)
                    }
                    if (result.type == MoviesType.UPCOMING) {
                        addToAdapter(upcomingMoviesView, result.moviesList)
                    }
                }
            }
        })
    }

    private fun addToAdapter(view: MoviesListView, list: List<Movie>?) {
        if (!list.isNullOrEmpty()) {
            if (list.size > 10) view.addItems(list.subList(0, 10))
            else view.addItems(list)
        }

    }
}