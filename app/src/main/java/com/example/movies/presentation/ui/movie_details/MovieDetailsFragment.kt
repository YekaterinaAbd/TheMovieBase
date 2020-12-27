package com.example.movies.presentation.ui.movie_details

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.changeFragment
import com.example.movies.core.extensions.showToast
import com.example.movies.data.model.movie.*
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.data.network.VIDEO_URL
import com.example.movies.presentation.ui.lists.SharedViewModel
import com.example.movies.presentation.ui.lists.movies.HorizontalFilmsAdapter
import com.example.movies.presentation.ui.lists.search.DiscoverFragment
import com.example.movies.presentation.utils.DateUtil
import com.example.movies.presentation.utils.FullScreenChromeClient
import com.example.movies.presentation.utils.constants.GENRES
import com.example.movies.presentation.utils.constants.KEYWORDS
import com.example.movies.presentation.utils.constants.MOVIE_ID
import com.example.movies.presentation.utils.constants.POSTER_PATH
import com.example.movies.presentation.utils.widgets.OverviewView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject

class MovieDetailsFragment : Fragment() {

    private var id: Int? = null

    private lateinit var skeletonScreen: ViewSkeletonScreen
    private lateinit var mainLayout: LinearLayout
    private lateinit var poster: ImageView
    private lateinit var name: TextView
    private lateinit var year: TextView
    private lateinit var duration: TextView
    private lateinit var tagline: TextView
    private lateinit var rating: TextView
    private lateinit var countries: TextView
    private lateinit var votesCount: TextView
    private lateinit var like: ImageView
    private lateinit var watchlist: ImageView
    private lateinit var rate: TextView
    private lateinit var userRating: TextView
    private lateinit var webView: WebView
    private lateinit var genresChipGroup: ChipGroup
    private lateinit var trailerLayout: LinearLayout
    private lateinit var similarMoviesLayout: LinearLayout
    private lateinit var keywordsLayout: LinearLayout
    private lateinit var overviewLayout: OverviewView
    private lateinit var errorLayout: FrameLayout
    private lateinit var rvSimilarMovies: RecyclerView
    private lateinit var rvCast: RecyclerView
    private lateinit var ivBack: ImageView
    private lateinit var director: TextView
    private lateinit var chipGroup: ChipGroup
    private lateinit var castLayout: LinearLayout

    private val viewModel: MovieDetailsViewModel by inject()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val itemClickListener = object : HorizontalFilmsAdapter.ItemClickListener {
        override fun itemClick(id: Int?) {
            if (id == null) return
            changeFragment<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(MOVIE_ID to id),
                animation = NavigationAnimation.CENTER
            )
        }
    }

    private val similarMoviesAdapter by lazy {
        HorizontalFilmsAdapter(itemClickListener)
    }

    private val castAdapter by lazy {
        CastAdapter()
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.get(MOVIE_ID) != null) {
            id = args.getInt(MOVIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        getMovie(id)
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.viewModelStore?.clear()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun bindViews(view: View) = with(view) {
        mainLayout = findViewById(R.id.mainLayout)
        poster = findViewById(R.id.poster)
        name = findViewById(R.id.name)
        year = findViewById(R.id.year)
        duration = findViewById(R.id.duration)
        tagline = findViewById(R.id.tagline)
        countries = findViewById(R.id.countries)
        rating = findViewById(R.id.rating)
        votesCount = findViewById(R.id.votesCount)
        like = findViewById(R.id.like)
        watchlist = findViewById(R.id.watchlist)
        rate = findViewById(R.id.rateMovie)
        userRating = findViewById(R.id.userRating)
        //requireActivity().themeModeImage.visibility = View.GONE
        webView = findViewById(R.id.webView)
        genresChipGroup = findViewById(R.id.genresChipGroup)
        trailerLayout = findViewById(R.id.trailerLayout)
        overviewLayout = findViewById(R.id.overviewLayout)
        similarMoviesLayout = findViewById(R.id.similarMoviesLayout)
        keywordsLayout = findViewById(R.id.keywordsLayout)
        rvSimilarMovies = findViewById(R.id.rvSimilarMovies)
        rvCast = findViewById(R.id.rvCast)
        // loadingLayout = findViewById(R.id.loadingLayout)
        errorLayout = findViewById(R.id.errorLayout)
        director = findViewById(R.id.director)
        chipGroup = findViewById(R.id.chipGroup)
        castLayout = findViewById(R.id.castLayout)
        ivBack = findViewById(R.id.ivBack)

        skeletonScreen = Skeleton.bind(mainLayout)
            .load(R.layout.movie_details_skeleton_view)
            .shimmer(true)
            .color(R.color.lightColorBackground)
            .duration(1000)
            .show()

        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        rate.setOnClickListener {
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
        rvSimilarMovies.adapter = similarMoviesAdapter

        rvCast.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvCast.adapter = castAdapter
    }

    private fun getMovie(id: Int?) {
        if (id != null) {
            viewModel.getMovie(id)
            viewModel.getTrailer(id)
            viewModel.getSimilarMovies(id)
            viewModel.getKeyWords(id)
            viewModel.getCredits(id)
            observeMovie()
        }
    }

    private fun observeMovie() {

        viewModel.liveData.observe(requireActivity(), { result ->
            when (result) {
                is MovieDetailsViewModel.State.HideLoading -> {
                    skeletonScreen.hide()
                }
                is MovieDetailsViewModel.State.Error -> {
                    errorLayout.visibility = View.VISIBLE
                    mainLayout.visibility = View.GONE
                }
                is MovieDetailsViewModel.State.Result -> {
                    if (result.movie != null) {
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
                        similarMoviesAdapter.addItems(result.movies)
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
                        setCrew(result.credits.crew)
                        setCast(result.credits.cast)
                    }
                }
                is MovieDetailsViewModel.State.RatingResult -> {
                    if (result.success) {
                        rate.text = getString(R.string.update_rating)
                    } else requireContext().showToast(getString(R.string.rating_failed))
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
            rate.text = context?.getString(R.string.update_rating)
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
            createChip(genre.genre, genresChipGroup) {
                changeFragment<DiscoverFragment>(
                    container = R.id.framenav,
                    bundle = bundleOf(GENRES to listOf(genre)),
                    animation = NavigationAnimation.BOTTOM
                )
            }
        }
    }

    private fun setKeywords(keywords: List<Keyword>) {
        if (keywords.isNullOrEmpty()) return
        for (keyword in keywords) {
            if (keyword.keyword == null) continue
            createChip(keyword.keyword, chipGroup) {
                changeFragment<DiscoverFragment>(
                    container = R.id.framenav,
                    bundle = bundleOf(KEYWORDS to listOf(keyword)),
                    animation = NavigationAnimation.BOTTOM
                )
            }
        }
    }

    private fun setCrew(crew: List<Crew>?) {
        if (crew.isNullOrEmpty()) return
        val directorName: String? = crew.find { it.job == "Director" }?.name
        director.text = directorName
    }

    private fun setCast(cast: List<Cast>?) {
        if (cast.isNullOrEmpty()) return
        castLayout.visibility = View.VISIBLE
        castAdapter.addAll(cast)
    }

    private fun createChip(title: String, chipGroup: ChipGroup, onClick: () -> Unit) {
        if (context == null) return
        val chip = LayoutInflater.from(context)
            .inflate(R.layout.item_chip_category, null, false) as Chip
        chip.text = title
        chip.setOnClickListener {
            onClick()
        }
        chipGroup.addView(chip)
    }

    private fun showRatingDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
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
