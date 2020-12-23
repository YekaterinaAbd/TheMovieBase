package com.example.movies.presentation.ui.lists.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.replaceFragments
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesType
import com.example.movies.presentation.ui.lists.MoviesListViewModel
import com.example.movies.presentation.ui.movie_details.MovieDetailsFragment
import com.example.movies.presentation.utils.constants.INTENT_KEY
import com.example.movies.presentation.utils.constants.MOVIE_CLICKED
import com.example.movies.presentation.utils.constants.MOVIE_ID
import com.example.movies.presentation.utils.constants.MOVIE_TITLE
import com.example.movies.presentation.utils.widgets.MoviesListView
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

class MainListsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var topMoviesView: MoviesListView
    private lateinit var currentMoviesView: MoviesListView
    private lateinit var upcomingMoviesView: MoviesListView
    private lateinit var popularMoviesView: MoviesListView

    private val moviesListViewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : SimpleItemClickListener {
        override fun itemClick(position: Int, item: Movie) {
            if (item.id == null) return
            logEvent(MOVIE_CLICKED, item)
            parentFragmentManager.replaceFragments<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(INTENT_KEY to item.id),
                animation = NavigationAnimation.CENTER
            )
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
        popularMoviesView = view.findViewById(R.id.popularMovies)

        topMoviesView.apply {
            setData(MoviesType.TOP)
            setListener(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        currentMoviesView.apply {
            setData(MoviesType.CURRENT)
            setListener(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        upcomingMoviesView.apply {
            setData(MoviesType.UPCOMING)
            setListener(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        popularMoviesView.apply {
            setData(MoviesType.POPULAR)
            setListener(parentFragmentManager)
            setAdapter(itemClickListener)
        }

        swipeRefreshLayout.setOnRefreshListener {
            clear()
            getMovies()
        }
    }

    private fun getMovies() {
        getMovies(MoviesType.TOP)
        getMovies(MoviesType.CURRENT)
        getMovies(MoviesType.UPCOMING)
        getMovies(MoviesType.POPULAR)
    }

    private fun getMovies(type: MoviesType) {
        moviesListViewModel.getMovies(type, 1)
    }

    private fun clear() {
        topMoviesView.clearAdapter()
        currentMoviesView.clearAdapter()
        upcomingMoviesView.clearAdapter()
        popularMoviesView.clearAdapter()
    }

    private fun observe() {
        moviesListViewModel.liveData.observe(viewLifecycleOwner, { result ->
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
                    if (result.type == MoviesType.POPULAR) {
                        addToAdapter(popularMoviesView, result.moviesList)
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

    private fun logEvent(logMessage: String, item: Movie) {
        val bundle = bundleOf(
            MOVIE_ID to item.id.toString(),
            MOVIE_TITLE to item.title
        )
        firebaseAnalytics.logEvent(logMessage, bundle)
    }
}