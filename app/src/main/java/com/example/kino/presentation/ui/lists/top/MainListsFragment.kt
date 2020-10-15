package com.example.kino.presentation.ui.lists.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.lists.MoviesType
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.example.kino.presentation.utils.constants.MOVIE_CLICKED
import com.example.kino.presentation.utils.constants.MOVIE_ID
import com.example.kino.presentation.utils.constants.MOVIE_TITLE
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

class MainListsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var currentRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var topTitle: TextView
    private lateinit var currentTitle: TextView

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

    private val topAdapter: HorizontalFilmsAdapter by lazy {
        HorizontalFilmsAdapter(itemClickListener = itemClickListener)
    }

    private val currentAdapter: HorizontalFilmsAdapter by lazy {
        HorizontalFilmsAdapter(itemClickListener = itemClickListener)
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
        setAdapter()
        getMovies(MoviesType.TOP)
        getMovies(MoviesType.CURRENT_PLAYING)
        observe()
    }

    private fun bindViews(view: View) {
        topRecyclerView = view.findViewById(R.id.filmsRecyclerView)
        currentRecyclerView = view.findViewById(R.id.currentFilmsRecyclerView)
        topTitle = view.findViewById(R.id.topTitle)
        currentTitle = view.findViewById(R.id.currentTitle)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        topTitle.setOnClickListener {
            openListFragment(MoviesType.TOP)
        }

        currentTitle.setOnClickListener {
            openListFragment(MoviesType.CURRENT_PLAYING)
        }

        swipeRefreshLayout.setOnRefreshListener {
            topAdapter.clearAll()
            currentAdapter.clearAll()
            getMovies(MoviesType.TOP)
            getMovies(MoviesType.CURRENT_PLAYING)
        }
    }

    private fun openListFragment(type: MoviesType) {
        val bundle = Bundle()
        bundle.putSerializable("type", type)

        val movieListsFragment = TopFilmsFragment()
        movieListsFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.framenav, movieListsFragment)
            .addToBackStack(null).commit()
    }

    private fun setAdapter() {

        topRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        currentRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        topRecyclerView.adapter = topAdapter
        currentRecyclerView.adapter = currentAdapter
    }

    private fun logEvent(logMessage: String, item: Movie) {
        val bundle = Bundle()
        bundle.putString(MOVIE_ID, item.id.toString())
        bundle.putString(MOVIE_TITLE, item.title)
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun getMovies(type: MoviesType) {
        moviesListViewModel.getMovies(type, 1)
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
                        result.moviesList?.let { topAdapter.addItems(it.subList(0, 10)) }
                    }
                    if (result.type == MoviesType.CURRENT_PLAYING) {
                        result.moviesList?.let { currentAdapter.addItems(it.subList(0, 10)) }
                    }
                }
            }
        })
    }
}