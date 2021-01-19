package com.example.movies.presentation.ui.lists.movies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.changeFragment
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesType
import com.example.movies.presentation.ui.MoviesState
import com.example.movies.presentation.ui.lists.MoviesListViewModel
import com.example.movies.presentation.ui.movie_details.MovieDetailsFragment
import com.example.movies.presentation.utils.constants.LOG_MOVIE_ID
import com.example.movies.presentation.utils.constants.LOG_MOVIE_TITLE
import com.example.movies.presentation.utils.constants.MOVIE_ID
import com.example.movies.presentation.utils.widgets.MoviesListView
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

class MainListsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var topMoviesView: MoviesListView
    private lateinit var currentMoviesView: MoviesListView
    private lateinit var upcomingMoviesView: MoviesListView
    private lateinit var popularMoviesView: MoviesListView

    private val moviesListViewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : HorizontalFilmsAdapter.ItemClickListener {
        override fun itemClick(id: Int?) {
            if (id == null) return
            //logEvent(MOVIE_CLICKED, item)
            changeFragment<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(MOVIE_ID to id),
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
        topMoviesView = view.findViewById(R.id.topMovies)
        currentMoviesView = view.findViewById(R.id.currentMovies)
        upcomingMoviesView = view.findViewById(R.id.upcomingMovies)
        popularMoviesView = view.findViewById(R.id.popularMovies)

        topMoviesView.apply {
            setData(MoviesType.TOP)
            setListener(this@MainListsFragment)
            setAdapter(itemClickListener)
        }

        currentMoviesView.apply {
            setData(MoviesType.CURRENT)
            setListener(this@MainListsFragment)
            setAdapter(itemClickListener)
        }

        upcomingMoviesView.apply {
            setData(MoviesType.UPCOMING)
            setListener(this@MainListsFragment)
            setAdapter(itemClickListener)
        }

        popularMoviesView.apply {
            setData(MoviesType.POPULAR)
            setListener(this@MainListsFragment)
            setAdapter(itemClickListener)
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

    private fun showSkeletonScreen() {
        topMoviesView.showSkeletonScreen()
        currentMoviesView.showSkeletonScreen()
        upcomingMoviesView.showSkeletonScreen()
        popularMoviesView.showSkeletonScreen()
    }

    private fun observe() {
        moviesListViewModel.liveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is MoviesState.ShowLoading -> {
                    showSkeletonScreen()
                }
                is MoviesState.HideLoading -> {
                }
                is MoviesState.Result -> {
                    if (result.type == MoviesType.TOP) {
                        topMoviesView.hideSkeletonScreen()
                        addToAdapter(topMoviesView, result.moviesList)
                    }
                    if (result.type == MoviesType.CURRENT) {
                        currentMoviesView.hideSkeletonScreen()
                        addToAdapter(currentMoviesView, result.moviesList)
                    }
                    if (result.type == MoviesType.UPCOMING) {
                        upcomingMoviesView.hideSkeletonScreen()
                        addToAdapter(upcomingMoviesView, result.moviesList)
                    }
                    if (result.type == MoviesType.POPULAR) {
                        popularMoviesView.hideSkeletonScreen()
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
            LOG_MOVIE_ID to item.id.toString(),
            LOG_MOVIE_TITLE to item.title
        )
        firebaseAnalytics.logEvent(logMessage, bundle)
    }
}