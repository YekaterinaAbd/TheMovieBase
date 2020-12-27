package com.example.movies.presentation.ui.lists.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.RecyclerViewSkeletonScreen
import com.ethanhua.skeleton.Skeleton
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.changeFragment
import com.example.movies.data.model.movie.Genre
import com.example.movies.data.model.movie.Keyword
import com.example.movies.domain.model.Movie
import com.example.movies.presentation.ui.MovieState
import com.example.movies.presentation.ui.lists.MoviesListViewModel
import com.example.movies.presentation.ui.lists.StatesBottomSheetFragment
import com.example.movies.presentation.ui.movie_details.MovieDetailsFragment
import com.example.movies.presentation.utils.constants.GENRES
import com.example.movies.presentation.utils.constants.KEYWORDS
import com.example.movies.presentation.utils.constants.MOVIE
import com.example.movies.presentation.utils.constants.MOVIE_ID
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import org.koin.android.ext.android.inject

class DiscoverFragment : Fragment() {

    private var genres: List<Genre>? = null
    private var keywords: List<Keyword>? = null

    private lateinit var skeletonScreen: RecyclerViewSkeletonScreen
    private lateinit var recyclerView: RecyclerView
    private lateinit var chipGroup: ChipGroup
    private lateinit var errorLayout: FrameLayout
    private lateinit var ivBack: ImageView

    private val viewModel: MoviesListViewModel by inject()

    private val itemClickListener = object : PaginationAdapter.ItemClickListener {
        override fun itemClick(id: Int?) {
            if (id == null) return
            changeFragment<MovieDetailsFragment>(
                container = R.id.framenav,
                bundle = bundleOf(MOVIE_ID to id),
                animation = NavigationAnimation.CENTER
            )
        }

        override fun openActionsDialog(item: Movie) {
            val addPhotoBottomDialogFragment: StatesBottomSheetFragment =
                StatesBottomSheetFragment.newInstance(
                    bundleOf(MOVIE to item)
                )
            addPhotoBottomDialogFragment.show(parentFragmentManager, "DIALOG_FRAGMENT")
        }
    }

    private val adapter by lazy {
        PaginationAdapter(itemClickListener = itemClickListener)
    }

    @Suppress("UNCHECKED_CAST")
    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        if (args?.get(GENRES) != null) {
            genres = args.get(GENRES) as List<Genre>
        }
        if (args?.get(KEYWORDS) != null) {
            keywords = args.get(KEYWORDS) as List<Keyword>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setAdapter()
        discoverMovies()
        observeMovies()
    }

    private fun bindViews(view: View) = with(view) {
        recyclerView = findViewById(R.id.filmsRecyclerView)
        errorLayout = findViewById(R.id.errorLayout)
        chipGroup = findViewById(R.id.chipGroup)
        ivBack = view.findViewById(R.id.ivBack)

        ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }

    private fun setAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        loadInitialSkeletonScreen()
    }

    private fun loadInitialSkeletonScreen() {
        skeletonScreen = Skeleton.bind(recyclerView)
            .adapter(adapter)
            .load(R.layout.film_object_skeleton_view)
            .shimmer(true)
            .color(R.color.lightColorBackground)
            .duration(1000)
            .show()
    }

    private fun mapGenres(): String? {
        if (genres.isNullOrEmpty()) return null
        var genreIds = ""
        for (genre in genres!!) {
            createChip(genre.genre, chipGroup)
            genreIds = genreIds + genre.genreId + ", "
        }
        return genreIds
    }

    private fun mapKeywords(): String? {
        if (keywords.isNullOrEmpty()) return null
        var keywordIds = ""
        for (keyword in keywords!!) {
            createChip(keyword.keyword, chipGroup)
            keywordIds = keywordIds + keyword.id + ", "
        }
        return keywordIds
    }

    private fun discoverMovies() {
        val genres = mapGenres()
        val keywords = mapKeywords()
        viewModel.discoverMovies(genres = genres, keywords = keywords)
    }

    private fun observeMovies() {

        viewModel.discoverPagedList.observe(viewLifecycleOwner, { list ->
            adapter.submitList(list)
        })

        viewModel.discoverStateLiveData.observe(viewLifecycleOwner, {
            when (it) {
                is MovieState.ShowLoading -> {
                    skeletonScreen.show()
                }
                is MovieState.HideLoading -> {
                    skeletonScreen.hide()
                }
                is MovieState.Error -> {
                    errorLayout.visibility = View.VISIBLE
                }
            }
            adapter.setNetworkState(it)
        })
    }

    private fun createChip(title: String?, chipGroup: ChipGroup) {
        if (title == null) return
        val chip = LayoutInflater.from(context)
            .inflate(R.layout.item_chip_category, null, false) as Chip
        chip.text = title
        chipGroup.addView(chip)
    }
}