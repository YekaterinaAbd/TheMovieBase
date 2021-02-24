package com.example.movies.presentation.ui.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.movies.R
import com.example.movies.domain.model.Movie
import com.example.movies.presentation.ui.MoviesState
import com.example.movies.presentation.utils.constants.MOVIE
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class StatesBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(data: Bundle? = null) = StatesBottomSheetFragment().apply {
            arguments = data
        }
    }

    private var movie: Movie? = null

    private lateinit var favourite: TextView
    private lateinit var watchlist: TextView
    private lateinit var rating: TextView

    private val viewModel: MoviesListViewModel by inject()

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.get(MOVIE) != null) {
            movie = args.get(MOVIE) as Movie
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        getMovieStates()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    private fun bindViews(view: View) = with(view) {
        favourite = findViewById(R.id.favourite)
        watchlist = findViewById(R.id.watchlist)
        rating = findViewById(R.id.rating)

        favourite.setOnClickListener {
            movie?.let { it1 -> viewModel.addToFavourites(it1) }
            bindData()
        }
        watchlist.setOnClickListener {
            movie?.let { it1 -> viewModel.addToWatchlist(it1) }
            bindData()
        }
    }

    private fun getMovieStates() {
        movie?.let { viewModel.getMovieStatuses(it) }
        viewModel.liveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is MoviesState.MovieStates -> {
                    movie?.isFavourite = result.favourite
                    movie?.isInWatchList = result.watchlist
                    movie?.rating = result.rating
                    bindData()
                }
            }
        }
    }

    private fun bindData() {
        if (movie?.isFavourite == true) {
            favourite.text = getString(R.string.remove_from_favourites)
            favourite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favourite, 0, 0, 0)
        } else {
            favourite.text = getString(R.string.add_to_favourites)
            favourite.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_favourite_border, 0, 0, 0
            )
        }
        if (movie?.isInWatchList == true) {
            watchlist.text = getString(R.string.remove_from_watchlist)
            watchlist.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_watchlist_filled, 0, 0, 0
            )
        } else {
            watchlist.text = getString(R.string.add_to_watchlist)
            watchlist.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_watchlist, 0, 0, 0
            )
        }
        if (movie?.rating != null && movie?.rating != 0.0) {
            rating.text = getString(R.string.my_rating_full, movie?.rating)
        } else {
            rating.text = getString(R.string.put_rating)
        }
    }
}
