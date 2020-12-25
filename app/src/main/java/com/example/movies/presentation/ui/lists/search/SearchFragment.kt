package com.example.movies.presentation.ui.lists.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.replaceFragments
import com.example.movies.data.model.entities.SearchQuery
import com.example.movies.presentation.ui.MovieState
import com.example.movies.presentation.ui.lists.MoviesListViewModel
import com.example.movies.presentation.ui.lists.movies.SimpleItemClickListener
import com.example.movies.presentation.ui.movie_details.MovieDetailsFragment
import com.example.movies.presentation.utils.constants.INTENT_KEY
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var search: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var recentMoviesRecyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchTipsLayout: LinearLayout
    private lateinit var clearQueries: ImageView
    private lateinit var clearRecentMovies: ImageView
    private lateinit var appbar: AppBarLayout
    private var query: String? = null

    private val searchViewModel: SearchViewModel by inject()
    private val moviesViewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : SimpleItemClickListener {
        override fun itemClick(id: Int?) {
            if (id == null) return
            parentFragmentManager.replaceFragments<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(INTENT_KEY to id),
                animation = NavigationAnimation.CENTER
            )
        }
    }

    private val onQueryClickListener = object : OnQueryClickListener {
        override fun onQueryClick(item: SearchQuery) {
            search.setQuery(item.query, true)
            search.isIconified = false
        }

        override fun onQueryRemove(item: SearchQuery) {
            searchViewModel.deleteQuery(item.id)
        }
    }

    private val adapter by lazy {
        PaginationAdapter(itemClickListener = itemClickListener)
    }

    private val searchHistoryAdapter by lazy {
        SearchHistoryAdapter(onQueryClickListener)
    }

    private val recentMoviesAdapter by lazy {
        RecentMovieAdapter(itemClickListener = itemClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        getSearchHistory()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recycler_view)
        searchHistoryRecyclerView = findViewById(R.id.search_history_recycler_view)
        recentMoviesRecyclerView = findViewById(R.id.recent_movies_recycler_view)
        searchTipsLayout = findViewById(R.id.searchTipsLayout)
        clearQueries = findViewById(R.id.clearQuery)
        clearRecentMovies = findViewById(R.id.clearMovies)
        search = findViewById(R.id.search)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        appbar = findViewById(R.id.appbar)

        val searchCloseButtonId: Int = search.context.resources
            .getIdentifier("android:id/search_close_btn", null, null)
        val closeButton: ImageView = search.findViewById(searchCloseButtonId) as ImageView

        closeButton.setOnClickListener {
            adapter.submitList(null)
            searchTipsLayout.visibility = View.VISIBLE
            search.setQuery("", false)
            query = null
            search.clearFocus()
        }

        clearQueries.setOnClickListener {
            searchViewModel.deleteAllQueries()
            searchHistoryAdapter.removeAll()
        }

        clearRecentMovies.setOnClickListener {
            searchViewModel.deleteRecentMovies()
            recentMoviesAdapter.clearAll()
        }

        swipeRefreshLayout.setOnRefreshListener {
            adapter.submitList(null)
            if (!query.isNullOrEmpty())
                query?.let { searchMovies(it) }
            swipeRefreshLayout.isRefreshing = false
        }

        search.setOnClickListener {
            search.isIconified = false
            appbar.setExpanded(false, true)
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                if (!p0.isNullOrEmpty()) {
                    searchTipsLayout.visibility = View.GONE
                    adapter.submitList(null)
                    query = p0
                    searchMovies(query!!)
                    insertQuery(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Start filtering the list as user start entering the characters
                return false
            }
        })
    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(context)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        recentMoviesRecyclerView.layoutManager = GridLayoutManager(context, 3)
        recentMoviesRecyclerView.adapter = recentMoviesAdapter
    }

    private fun getSearchHistory() {
        searchViewModel.getLastQueries()
        searchViewModel.getRecentMovies()
        searchViewModel.historyLiveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is SearchViewModel.HistoryState.Result -> {
                    if (!result.queries.isNullOrEmpty()) {
                        searchHistoryAdapter.addItems(result.queries)
                    } else {
                        searchHistoryRecyclerView.visibility = View.GONE
                    }
                }
            }
        })
        searchViewModel.recentMovieLiveData.observe(viewLifecycleOwner, { result ->
            when (result) {
                is SearchViewModel.RecentMovieState.Result -> {
                    recentMoviesAdapter.addItems(result.movies)
                }
            }
        })
    }

    private fun insertQuery(query: String?) {
        if (!query.isNullOrEmpty()) {
            searchViewModel.insertQuery(query)
        }
    }

    private fun searchMovies(query: String) {
        moviesViewModel.searchMovies(query)

        moviesViewModel.searchPagedList.observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
        })

        moviesViewModel.searchStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is MovieState.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MovieState.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieState.HidePageLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MovieState.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
            adapter.setNetworkState(it)
        })
    }
}
