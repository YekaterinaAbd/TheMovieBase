package com.example.movies.presentation.utils.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.ViewSkeletonScreen
import com.example.movies.R
import com.example.movies.core.NavigationAnimation
import com.example.movies.core.extensions.changeFragment
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesType
import com.example.movies.presentation.ui.lists.movies.HorizontalFilmsAdapter
import com.example.movies.presentation.ui.lists.movies.MoviesFragment
import com.example.movies.presentation.utils.constants.MOVIE_TYPE

class MoviesListView : LinearLayout {

    private lateinit var skeletonScreen: ViewSkeletonScreen

    private lateinit var llTitle: LinearLayout
    private lateinit var title: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HorizontalFilmsAdapter
    private lateinit var type: MoviesType

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    fun setData(movieType: MoviesType) {
        type = movieType
        title.text = movieType.type

        skeletonScreen = Skeleton.bind(this)
            .load(R.layout.list_skeleton_view)
            .shimmer(true)
            .color(android.R.color.white)
            .duration(1000)
            .show()
    }

    fun showSkeletonScreen() {
        skeletonScreen.show()
    }

    fun hideSkeletonScreen() {
        skeletonScreen.hide()
    }

    fun setListener(fragment: Fragment) {
        llTitle.setOnClickListener {
            openListFragment(fragment, type)
        }
    }

    fun setAdapter(clickListener: HorizontalFilmsAdapter.ItemClickListener) {
        adapter = HorizontalFilmsAdapter(clickListener)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    fun clearAdapter() {
        adapter.clearAll()
    }

    fun addItems(moviesList: List<Movie>) {
        adapter.addItems(moviesList)
    }

    private fun openListFragment(fragment: Fragment, movieType: MoviesType) {
        fragment.changeFragment<MoviesFragment>(
            container = R.id.framenav,
            bundle = bundleOf(
                MOVIE_TYPE to movieType
            ),
            animation = NavigationAnimation.SLIDE_RIGHT
        )
    }

    private fun init(context: Context) {
        inflate(context, R.layout.list_view, this)
        title = findViewById(R.id.tvTitle)
        llTitle = findViewById(R.id.llTitle)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        init(context)
    }
}
