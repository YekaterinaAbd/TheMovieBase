package com.example.kino.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.utils.Constants
import com.example.kino.utils.FragmentEnum
import com.example.kino.model.movie.Movie
import com.example.kino.view.RecyclerViewAdapter
import com.example.kino.view.activities.MovieDetailActivity
import com.example.kino.view_model.MoviesListViewModel
import com.example.kino.view_model.ViewModelProviderFactory

class FilmsFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var moviesListViewModel: MoviesListViewModel
    private val constants: Constants =
        Constants()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        bindViews(view)
        setAdapter()
        getMovies()
    }

    private fun setViewModel() {
        val viewModelProviderFactory = ViewModelProviderFactory(context = requireActivity())
        moviesListViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MoviesListViewModel::class.java)

    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.filmsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies()
        }
    }

    private fun setAdapter() {
        recyclerViewAdapter =
            RecyclerViewAdapter(itemClickListener = this)
        recyclerView.adapter = recyclerViewAdapter
    }

    override fun itemClick(position: Int, item: Movie) {
        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra(constants.intentKey, item.id)
        startActivity(intent)
    }

    override fun addToFavourites(position: Int, item: Movie) {
        moviesListViewModel.addToFavourites(item)
    }

    private fun getMovies() {
        moviesListViewModel.getMovies(FragmentEnum.TOP)
        moviesListViewModel.liveData.observe(this, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    result.moviesList?.let { recyclerViewAdapter?.replaceItems(it) }
                }
                is MoviesListViewModel.State.Update -> {
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            }
        })
    }
}