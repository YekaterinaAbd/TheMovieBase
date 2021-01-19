package com.example.movies.presentation.ui.lists.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.domain.model.Movie
import com.example.movies.presentation.ui.LoadingState
import com.example.movies.presentation.utils.LoadMoreItemViewHolder
import com.squareup.picasso.Picasso


class PaginationAdapter(
    private val itemClickListener: ItemClickListener? = null
) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(DiffUtilCallBack) {

    private var state: LoadingState = LoadingState.ShowPageLoading

    companion object {

        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_DATA = 1

        val DiffUtilCallBack = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DATA -> MovieViewHolder(
                inflater.inflate(R.layout.film_object_view, parent, false)
            )
            VIEW_TYPE_LOADING -> LoadMoreItemViewHolder<LoadingState>(
                inflater.inflate(R.layout.loader, parent, false)
            )
            else -> throw Throwable("invalid view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_DATA) getItem(position)?.let {
            (holder as MovieViewHolder).bind(it)
        } else (holder as LoadMoreItemViewHolder<LoadingState>).bind(state)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) VIEW_TYPE_DATA else VIEW_TYPE_LOADING
    }

    fun setNetworkState(newState: LoadingState) {
        val previousState: LoadingState = this.state
        val previousExtraRow = hasExtraRow()
        this.state = newState
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    private fun hasExtraRow(): Boolean {
        return state !== LoadingState.HidePageLoading
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        private val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
        private val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
        private val poster = view.findViewById<ImageView>(R.id.poster)
        private val tvRating = view.findViewById<TextView>(R.id.tvRating)
        private val verticalDots = view.findViewById<ImageView>(R.id.verticalDots)

        fun bind(movie: Movie?) {
            if (movie == null) return

            verticalDots.visibility = View.VISIBLE

            tvTitle.text = movie.title
            if (!movie.releaseDate.isNullOrEmpty())
                tvReleaseDate.text = movie.releaseDate.substring(0, 4)
            tvRating.text = movie.voteAverage.toString()
            tvGenres.text = movie.genreNames

            if (!movie.posterPath.isNullOrEmpty())
                Picasso.get()
                    .load(IMAGE_URL + movie.posterPath)
                    .into(poster)


            view.setOnClickListener {
                itemClickListener?.itemClick(movie.id)
            }

            verticalDots.setOnClickListener {
                itemClickListener?.openActionsDialog(movie)
            }
        }
    }

    interface ItemClickListener {
        fun itemClick(id: Int?)
        fun openActionsDialog(item: Movie)
    }
}
