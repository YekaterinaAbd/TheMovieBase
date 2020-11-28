package com.example.kino.presentation.utils.widgets

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.data.model.movie.MoviesType
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.lists.top.HorizontalFilmsAdapter
import com.example.kino.presentation.ui.lists.top.TopFilmsFragment
import com.example.kino.presentation.utils.constants.MOVIE_TYPE

class MoviesListView : LinearLayout {

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
        title.text = MoviesType.typeToString(type)
    }

    fun setListeners(fm: FragmentManager) {
        llTitle.setOnClickListener {
            openListFragment(type, fm)
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

    private fun openListFragment(type: MoviesType, fm: FragmentManager) {
        val bundle = Bundle()
        bundle.putSerializable(MOVIE_TYPE, type)

        val movieListsFragment = TopFilmsFragment()
        movieListsFragment.arguments = bundle
        fm.beginTransaction().add(R.id.framenav, movieListsFragment)
            .addToBackStack(null).hide(this.findFragment()).commit()
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
