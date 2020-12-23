package com.example.movies.presentation.ui.movie_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movies.R
import com.example.movies.core.extensions.replaceFragments
import com.example.movies.core.extensions.showToast
import com.example.movies.data.model.movie.*
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.data.network.VIDEO_URL
import com.example.movies.domain.model.Movie
import com.example.movies.presentation.ui.lists.SharedViewModel
import com.example.movies.presentation.ui.lists.movies.HorizontalFilmsAdapter
import com.example.movies.presentation.ui.lists.movies.SimpleItemClickListener
import com.example.movies.presentation.utils.DateUtil
import com.example.movies.presentation.utils.FullScreenChromeClient
import com.example.movies.presentation.utils.constants.INTENT_KEY
import com.example.movies.presentation.utils.constants.POSTER_PATH
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import org.koin.android.ext.android.inject


class MovieDetailsFragment : Fragment() {

    private var id: Int? = null

    private val viewModel: MovieDetailsViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val itemClickListener = object : SimpleItemClickListener {
        override fun itemClick(position: Int, item: Movie) {
            if (item.id == null) return
            parentFragmentManager.replaceFragments<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(INTENT_KEY to item.id)
            )
        }
    }

    private val adapter by lazy {
        HorizontalFilmsAdapter(itemClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val bundle = this.arguments
        if (bundle != null) id = bundle.getInt(INTENT_KEY)
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        getMovie(id)
        observeMovie()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun bindViews(view: View) = with(view) {

        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        rateMovie.setOnClickListener {
            showRatingDialog()
        }

        webView.apply {
            clearCache(true)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowContentAccess = true
            settings.setAppCacheEnabled(true)
            settings.saveFormData = true
            settings.allowFileAccess = true
            settings.allowFileAccessFromFileURLs = true
            webChromeClient = FullScreenChromeClient(requireActivity())
            settings.allowUniversalAccessFromFileURLs = true
            if (Build.VERSION.SDK_INT >= 26) {
                settings.safeBrowsingEnabled = false
            }
        }
    }

    private fun setAdapter() {
        rvSimilarMovies.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSimilarMovies.adapter = adapter
    }

    private fun getMovie(id: Int?) {
        if (id != null) {
            viewModel.getMovie(id)
            viewModel.getTrailer(id)
            viewModel.getSimilarMovies(id)
            viewModel.getKeyWords(id)
            viewModel.getCredits(id)
        }
    }

    private fun observeMovie() {

        viewModel.liveData.observe(requireActivity(), { result ->
            when (result) {
                is MovieDetailsViewModel.State.HideLoading -> {
                    progressBar.visibility = View.GONE
                }
                is MovieDetailsViewModel.State.Error -> {
                    errorLayout.visibility = View.VISIBLE
                }
                is MovieDetailsViewModel.State.Result -> {
                    if (result.movie != null) {
                        loadingLayout.visibility = View.GONE
                        bindData(result.movie)
                    }
                }
                is MovieDetailsViewModel.State.TrailerResult -> {
                    if (result.trailer?.id != null) {
                        trailerLayout.visibility = View.VISIBLE
                        setTrailer(result.trailer)
                    }
                }
                is MovieDetailsViewModel.State.SimilarMoviesResult -> {
                    if (!result.movies.isNullOrEmpty()) {
                        adapter.addItems(result.movies)
                        similarMoviesLayout.visibility = View.VISIBLE
                    }

                }
                is MovieDetailsViewModel.State.KeyWordsListResult -> {
                    if (!result.keywords.isNullOrEmpty()) {
                        keywordsLayout.visibility = View.VISIBLE
                        setKeywords(result.keywords)
                    }
                }
                is MovieDetailsViewModel.State.CreditsResult -> {
                    if (result.credits != null) {
                        setCredits(result.credits)
                    }
                }
                is MovieDetailsViewModel.State.RatingResult -> {
                    if (result.success) {
                        rateMovie.text = getString(R.string.update_rating)
                        showToast(getString(R.string.movie_rated))
                    } else showToast(getString(R.string.rating_failed))
                }
            }
        })
    }

    private fun bindData(movie: MovieDetails) {
        name.text = movie.title
        year.text = DateUtil.convertIsoToDate(movie.releaseDate)
        rating.text = movie.voteAverage.toString()
        votesCount.text = context?.getString(R.string.voted, movie.voteCount)
        overviewLayout.setOverviewText(movie.overview)

        if (movie.userRating != null) {
            userRating.text = movie.userRating.toString()
            rateMovie.text = getString(R.string.update_rating)
        }

        setGenres(movie.genres)
        Picasso.get().load(IMAGE_URL + movie.posterPath).into(poster)

        if (movie.runtime != null) {
            val runtime: String = movie.runtime.toString()
            duration.text = context?.getString(R.string.duration_time, runtime)
        }

        if (!movie.tagLine.isNullOrEmpty()) {
            val tagLineText: String = movie.tagLine.toString()
            tagline.text = context?.getString(R.string.tagline, tagLineText)
        }

        if (!movie.countries.isNullOrEmpty()) {
            countries.text = setCountries(movie.countries)
        }

        if (movie.favourite) like.setImageResource(R.drawable.ic_favourite)
        else like.setImageResource(R.drawable.ic_favourite_border)

        if (movie.watchlist) watchlist.setImageResource(R.drawable.ic_watchlist_filled)
        else watchlist.setImageResource(R.drawable.ic_watchlist)

        like.setOnClickListener {
            if (movie.favourite) {
                movie.favourite = false
                like.setImageResource(R.drawable.ic_favourite_border)
            } else {
                movie.favourite = true
                like.setImageResource(R.drawable.ic_favourite)
            }
            movie.id?.let { it1 ->
                viewModel.updateFavouriteStatus(
                    it1,
                    movie.favourite
                )
            }
            //sharedViewModel.setMovie(movie)
        }

        watchlist.setOnClickListener {
            if (movie.watchlist) {
                movie.watchlist = false
                watchlist.setImageResource(R.drawable.ic_watchlist)
            } else {
                movie.watchlist = true
                watchlist.setImageResource(R.drawable.ic_watchlist_filled)
            }
            movie.id?.let { it1 ->
                viewModel.updateWatchListStatus(
                    it1,
                    movie.watchlist
                )
            }
            //sharedViewModel.setMovie(movie)
        }

        poster.setOnClickListener {
            if (!movie.posterPath.isNullOrEmpty()) {
                val bundle = Bundle()
                bundle.putString(POSTER_PATH, movie.posterPath)

                val fragmentFullScreenPoster = FullScreenPosterFragment()
                fragmentFullScreenPoster.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .add(R.id.framenav, fragmentFullScreenPoster)
                    .addToBackStack(null).commit()
            }
        }
    }

    private fun setCountries(countries: List<Country>): String {
        var countriesString = ""
        countries.forEach { country ->
            countriesString += context?.getString(R.string.genre_name, country.country)
        }
        if (countries.isNotEmpty())
            countriesString = countriesString.substring(0, countriesString.length - 2)

        return countriesString
    }

    private fun setTrailer(trailer: Video) {
        webView.loadUrl(VIDEO_URL + trailer.link)
    }

    private fun setGenres(genres: List<Genre>?) {
        if (genres.isNullOrEmpty()) return
        for (genre in genres) {
            if (genre.genre == null) continue
            createChip(genre.genre, genresChipGroup)
        }
    }

    private fun setKeywords(keywords: List<KeyWord>) {
        if (keywords.isNullOrEmpty()) return
        for (keyword in keywords) {
            keyword.keyword?.let { createChip(it, chipGroup) }
        }
    }

    private fun setCredits(credits: Credits) {
        if (credits.crew.isNullOrEmpty()) return
        val directorName = credits.crew.find { crew -> crew.job == "Director" }?.name
        director.text = directorName
    }

    private fun createChip(title: String, chipGroup: ChipGroup) {
        if (context == null) return
        val chip = LayoutInflater.from(context)
            .inflate(R.layout.item_chip_category, null, false) as Chip
        chip.text = title
        chipGroup.addView(chip)

    }

    private fun showRatingDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_rating)

        val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)
        val ratingText = dialog.findViewById<TextView>(R.id.ratingText)
        val applyBtn = dialog.findViewById<TextView>(R.id.applyBtn)
        val cancelBtn = dialog.findViewById<TextView>(R.id.cancelBtn)

        ratingBar.stepSize = 0.25f
        ratingText.text = getString(R.string.rating, 0.toFloat())

        ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                ratingText.text = getString(R.string.rating, (rating * 2))
            }

        applyBtn.setOnClickListener {
            dialog.dismiss()
            val rating: Double = (ratingBar.rating * 2).toDouble()
            if (id != null) viewModel.rateMovie(id!!, rating)
            userRating.text = rating.toString()
        }

        cancelBtn.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }
}
