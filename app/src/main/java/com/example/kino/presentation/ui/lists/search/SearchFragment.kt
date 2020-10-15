package com.example.kino.presentation.ui.lists.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.lists.SharedViewModel
import com.example.kino.presentation.ui.lists.top.TopAdapter
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.example.kino.presentation.utils.pagination.PaginationScrollListener
import org.koin.android.ext.android.inject

class SearchFragment : Fragment(), TopAdapter.RecyclerViewItemClick {

    private lateinit var search: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private var query: String? = null

    private val viewModel: MoviesListViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var currentPage = PaginationScrollListener.PAGE_START
    private var isLocal = false
    private var isLastPage = false
    private var isLoading = false
    private var itemCount = 0

    private val adapter: TopAdapter by lazy {
        TopAdapter(itemClickListener = this, searchFragment = true)
    }

    override fun itemClick(position: Int, item: Movie) {
        val bundle = Bundle()
        bundle.putInt(INTENT_KEY, item.id)

        val movieDetailedFragment = MovieDetailsFragment()
        movieDetailedFragment.arguments = bundle

        parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
            .addToBackStack(null).commit()
        //requireActivity().toolbar.visibility = View.GONE
        //requireActivity().bottomNavigation.visibility = View.GONE
    }

    override fun addToFavourites(position: Int, item: Movie) {
        viewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
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
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.recycler_view)
        search = findViewById(R.id.search)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        search.setOnClickListener { }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                if (!p0.isNullOrEmpty()) {
                    adapter.clearAll()
                    currentPage = PaginationScrollListener.PAGE_START
                    isLastPage = false
                    recyclerView.scrollToPosition(0)
                    query = p0
                    searchMovies(query!!, 1)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Start filtering the list as user start entering the characters
                return false
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            itemCount = 0
            currentPage = PaginationScrollListener.PAGE_START
            isLastPage = false
            recyclerView.scrollToPosition(0)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setAdapter() {
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                if (!query.isNullOrEmpty()) {
                    searchMovies(query!!, currentPage)
                }
            }

            override fun isLastPage(): Boolean = isLastPage
            override fun isLoading(): Boolean = isLoading
            override fun isLocal(): Boolean = isLocal

        })
    }

    private fun searchMovies(query: String, page: Int) {
        viewModel.searchMovies(query, page)

        viewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    adapter.removeLoading()
                    adapter.addItems(result.moviesList ?: emptyList())
                    adapter.addLoading()
                    isLoading = false
                }
                is MoviesListViewModel.State.Update -> {
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }
}
