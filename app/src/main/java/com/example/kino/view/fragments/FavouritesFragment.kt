package com.example.kino.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.model.movie.Movie
import com.example.kino.utils.AppContainer
import com.example.kino.utils.FragmentEnum
import com.example.kino.utils.constants.INTENT_KEY
import com.example.kino.view.adapters.FavouritesAdapter
import com.example.kino.view_model.MoviesListViewModel
import com.example.kino.view_model.SharedViewModel
import kotlinx.android.synthetic.main.activity_main.*

class FavouritesFragment : Fragment(), FavouritesAdapter.RecyclerViewItemClick {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var recyclerViewAdapter: FavouritesAdapter? = null

    private lateinit var moviesListViewModel: MoviesListViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sharedViewModel.liked.observe(viewLifecycleOwner, Observer { item ->
            if (item.isClicked) recyclerViewAdapter?.addItem(item)
            else recyclerViewAdapter?.removeItem(item)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favourites_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        bindViews(view)
        setAdapter()
        getMovies()
    }

    private fun setViewModel() {
        moviesListViewModel =
            MoviesListViewModel(requireContext(), AppContainer.getMovieRepository())
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter?.clearAll()
            getMovies()
        }
    }

    private fun setAdapter() {
        recyclerViewAdapter =
            FavouritesAdapter(itemClickListener = this)

        recyclerView.adapter = recyclerViewAdapter
    }

    override fun itemClick(position: Int, item: Movie) {
        val bundle = Bundle()
        bundle.putInt(INTENT_KEY, item.id)

        val movieDetailedFragment = MovieDetailsFragment()
        movieDetailedFragment.arguments = bundle
        parentFragmentManager.beginTransaction().add(R.id.frame, movieDetailedFragment)
            .addToBackStack(null)
            .commit()
        requireActivity().toolbar.visibility = View.GONE
        requireActivity().bottomNavigation.visibility = View.GONE
    }

    override fun addToFavourites(position: Int, item: Movie) {
        moviesListViewModel.addToFavourites(item)
        sharedViewModel.setMovie(item)
    }

    private fun getMovies() {
        moviesListViewModel.getMovies(FragmentEnum.FAVOURITES, 1)
        moviesListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    result.moviesList?.let { recyclerViewAdapter?.addItems(it) }
                }
                is MoviesListViewModel.State.Update -> {
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            }
        })
    }
}