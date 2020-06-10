package com.example.kino.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.model.database.MovieDao
import com.example.kino.model.database.MovieDatabase
import com.example.kino.model.database.MovieStatusDao
import com.example.kino.model.movie.Movie
import com.example.kino.model.repository.MovieRepositoryImpl
import com.example.kino.utils.*
import com.example.kino.view.RecyclerViewAdapter
import com.example.kino.view.activities.MovieDetailActivity
import com.example.kino.view_model.MoviesListViewModel
import com.google.firebase.analytics.FirebaseAnalytics


class FilmsFragment : Fragment(), RecyclerViewAdapter.RecyclerViewItemClick {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var recyclerView: RecyclerView
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var moviesListViewModel: MoviesListViewModel
    private var page: Int = 2
//    private val onScrolled = object:RecyclerView.OnScrollListener(){
//        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//            super.onScrolled(recyclerView, dx, dy)
//            val visibleItemCount = layoutManager.childCount
//            val totalItemCount = layoutManager.itemCount
//            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                && firstVisibleItemPosition >= 0
//                && totalItemCount >= page) {
//            }
//        }
//    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.films_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        setViewModel()
        bindViews(view)
        setAdapter()
//        recyclerView.addOnScrollListener(onScrolled)
    }

    private fun setViewModel() {
        val movieDao: MovieDao = MovieDatabase.getDatabase(requireContext()).movieDao()
        val movieStatusDao: MovieStatusDao =
            MovieDatabase.getDatabase(requireContext()).movieStatusDao()
        val movieRepository =
            MovieRepositoryImpl(movieDao, RetrofitService.getPostApi(), movieStatusDao)
        moviesListViewModel = MoviesListViewModel(requireContext(), movieRepository)
    }

    private fun bindViews(view: View) {
        recyclerView = view.findViewById(R.id.filmsRecyclerView)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies(page)
        }
    }

    private fun setAdapter() {
        recyclerViewAdapter =
            RecyclerViewAdapter(itemClickListener = this)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun logEvent(logMessage: String, item: Movie) {
        val bundle = Bundle()
        bundle.putString(MOVIE_ID, item.id.toString())
        bundle.putString(MOVIE_TITLE, item.title)
        firebaseAnalytics.logEvent(logMessage, bundle)
    }

    override fun itemClick(position: Int, item: Movie) {

        logEvent(MOVIE_CLICKED, item)

        val intent = Intent(context, MovieDetailActivity::class.java)
        intent.putExtra(INTENT_KEY, item.id)
        startActivity(intent)
    }

    override fun addToFavourites(position: Int, item: Movie) {
        if (!item.isClicked) logEvent(MOVIE_LIKED, item)
        moviesListViewModel.addToFavourites(item)
    }

    private fun getMovies(page:Int) {
        moviesListViewModel.getMovies(FragmentEnum.TOP, page)
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