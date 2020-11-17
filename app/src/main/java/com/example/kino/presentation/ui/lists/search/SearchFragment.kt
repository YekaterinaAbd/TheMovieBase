package com.example.kino.presentation.ui.lists.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.MovieState
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.lists.SharedViewModel
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.google.android.material.appbar.AppBarLayout
import org.koin.android.ext.android.inject

class SearchFragment : Fragment() {

    private lateinit var search: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var appbar: AppBarLayout
    private lateinit var layoutManager: LinearLayoutManager
    private var query: String? = null

    private val viewModel: MoviesListViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val adapter by lazy {
        PaginationAdapter(itemClickListener = itemClickListener, searchFragment = true)
    }

    private val itemClickListener = object : PaginationAdapter.RecyclerViewItemClick {
        override fun itemClick(position: Int, item: Movie) {
            val bundle = Bundle()
            bundle.putInt(INTENT_KEY, item.id)

            val movieDetailedFragment = MovieDetailsFragment()
            movieDetailedFragment.arguments = bundle

            parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
                .addToBackStack(null).commit()
        }

        override fun addToFavourites(position: Int, item: Movie) {
            viewModel.addToFavourites(item)
            sharedViewModel.setMovie(item)
        }
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
        appbar = findViewById(R.id.appbar)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        search.setOnClickListener {
            search.isIconified = false
            appbar.setExpanded(false, true)
        }

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //Performs search when user hit the search button on the keyboard
                if (!p0.isNullOrEmpty()) {
                    adapter.submitList(null)
                    query = p0
                    searchMovies(query!!)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Start filtering the list as user start entering the characters
                return false
            }
        })

        swipeRefreshLayout.setOnRefreshListener {
            recyclerView.scrollToPosition(0)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setAdapter() {
        recyclerView.adapter = adapter
    }

    private fun searchMovies(query: String) {
        viewModel.searchMovies(query)

        viewModel.searchPagedList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        viewModel.searchStateLiveData.observe(viewLifecycleOwner, {
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
