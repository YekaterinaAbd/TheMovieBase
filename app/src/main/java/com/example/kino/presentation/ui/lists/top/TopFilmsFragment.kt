package com.example.kino.presentation.ui.lists.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.data.mapper.DataSource
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.lists.SharedViewModel
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.*
import com.example.kino.presentation.utils.pagination.PaginationScrollListener
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.android.inject

class TopFilmsFragment : Fragment() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView

    private var movieType: MoviesType = MoviesType.TOP

    private var currentPage = PaginationScrollListener.PAGE_START
    private var isLocal = false
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0

    private val moviesListViewModel: MoviesListViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val itemClickListener = object : ItemClickListener {
        override fun itemClick(position: Int, item: Movie) {
            logEvent(MOVIE_CLICKED, item)

            val bundle = Bundle()
            bundle.putInt(INTENT_KEY, item.id)

            val movieDetailedFragment = MovieDetailsFragment()
            movieDetailedFragment.arguments = bundle

            parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
                .addToBackStack(null)
                .hide(this@TopFilmsFragment)
                .commit()
        }

        override fun addToFavourites(position: Int, item: Movie) {
            if (!item.isFavourite) logEvent(MOVIE_LIKED, item)
            moviesListViewModel.addToFavourites(item)
            sharedViewModel.setMovie(item)
        }

        override fun addToWatchlist(position: Int, item: Movie) {
            moviesListViewModel.addToWatchlist(item)
            //sharedViewModel.setMovie(item)
        }
    }

    private val adapter: TopAdapter by lazy {
        TopAdapter(itemClickListener = itemClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val bundle = this.arguments

        if (bundle != null) {
            movieType = bundle.get(MOVIE_TYPE) as MoviesType
        }
        return inflater.inflate(R.layout.fragment_films, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel.liked.observe(requireActivity(), Observer { item ->
            adapter.updateItem(item)
        })

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        bindViews(view)
        setAdapter()
        getMovies(currentPage)
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.filmsRecyclerView)
        tvTitle = view.findViewById(R.id.tvTitle)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        tvTitle.text = MoviesType.typeToString(movieType)

        ivBack = view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearAll()
            itemCount = 0
            currentPage = PaginationScrollListener.PAGE_START
            isLastPage = false
            getMovies(currentPage)
        }
    }

    private fun setAdapter() {
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                getMovies(currentPage)
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
            override fun isLocal(): Boolean = isLocal

        })
    }

    private fun logEvent(logMessage: String, item: Movie) {
        val bundle = Bundle()
        bundle.putString(MOVIE_ID, item.id.toString())
        bundle.putString(MOVIE_TITLE, item.title)
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    private fun getMovies(page: Int) {
        moviesListViewModel.getMovies(movieType, page)
        moviesListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    if (result.dataSource == DataSource.LOCAL) {
                        adapter.replaceItems(result.moviesList ?: emptyList())
                    } else {
                        adapter.removeLoading()
                        adapter.addItems(result.moviesList ?: emptyList())
                        adapter.addLoading()
                        isLoading = false
                    }
                }
                is MoviesListViewModel.State.Update -> {
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}