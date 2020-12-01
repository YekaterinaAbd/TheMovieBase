package com.example.kino.presentation.ui.lists.favouries

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
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kino.R
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.MoviesListViewModel
import com.example.kino.presentation.ui.lists.SharedViewModel
import com.example.kino.presentation.ui.lists.top.ItemClickListener
import com.example.kino.presentation.ui.movie_details.MovieDetailsFragment
import com.example.kino.presentation.utils.constants.INTENT_KEY
import com.example.kino.presentation.utils.constants.MOVIE_TYPE
import org.koin.android.ext.android.inject

class FavouritesFragment : Fragment() {

    private var movieType = MoviesType.FAVOURITES

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var ivBack: ImageView
    private lateinit var tvTitle: TextView

    private val moviesListViewModel: MoviesListViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val itemClickListener = object : ItemClickListener {
        override fun itemClick(item: Movie) {
            val bundle = Bundle()
            bundle.putInt(INTENT_KEY, item.id)

            val movieDetailedFragment = MovieDetailsFragment()
            movieDetailedFragment.arguments = bundle
            parentFragmentManager.beginTransaction().add(R.id.framenav, movieDetailedFragment)
                .addToBackStack(null)
                .commit()
        }

        override fun addToFavourites(item: Movie) {
            moviesListViewModel.addToFavourites(item)
            sharedViewModel.setMovie(item)
        }

        override fun addToWatchlist(item: Movie) {
            moviesListViewModel.addToWatchlist(item)
        }
    }

    private val recyclerViewAdapter: FavouritesAdapter by lazy {
        FavouritesAdapter(
            itemClickListener = itemClickListener,
            isWatchlist = movieType == MoviesType.WATCH_LIST
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        sharedViewModel.liked.observe(viewLifecycleOwner, Observer { item ->
//            if (item.isFavourite) recyclerViewAdapter.addItem(item)
//            else recyclerViewAdapter.removeItem(item)
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val bundle = this.arguments
        if (bundle != null) {
            movieType = bundle.getSerializable(MOVIE_TYPE) as MoviesType
        }
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        getMovies()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = view.findViewById(R.id.favRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            recyclerViewAdapter.clearAll()
            getMovies()
        }

        ivBack = view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        tvTitle = view.findViewById(R.id.tvTitle)
        tvTitle.text = MoviesType.typeToString(movieType)
    }

    private fun setAdapter() {
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerViewAdapter.setHasStableIds(true)
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun getMovies() {
        moviesListViewModel.getMovies(movieType, 1)
        moviesListViewModel.liveData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is MoviesListViewModel.State.ShowLoading -> {
                    swipeRefreshLayout.isRefreshing = true
                }
                is MoviesListViewModel.State.HideLoading -> {
                    swipeRefreshLayout.isRefreshing = false
                }
                is MoviesListViewModel.State.Result -> {
                    result.moviesList?.let { recyclerViewAdapter.addItems(it) }
                }
                is MoviesListViewModel.State.Update -> {
                    //recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        })
    }
}
