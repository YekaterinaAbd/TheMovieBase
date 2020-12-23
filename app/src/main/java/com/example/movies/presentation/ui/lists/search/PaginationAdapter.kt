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
import com.example.movies.presentation.ui.MovieState
import com.example.movies.presentation.ui.lists.movies.SimpleItemClickListener
import com.example.movies.presentation.utils.LoadMoreItemViewHolder
import com.squareup.picasso.Picasso

class PaginationAdapter(
    private val itemClickListener: SimpleItemClickListener? = null
) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(DiffUtilCallBack) {

    private var state: MovieState = MovieState.ShowPageLoading

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
            VIEW_TYPE_LOADING -> LoadMoreItemViewHolder<MovieState>(
                inflater.inflate(R.layout.loader, parent, false)
            )
            else -> throw Throwable("invalid view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_DATA) getItem(position)?.let {
            (holder as MovieViewHolder).bind(it)
        } else (holder as LoadMoreItemViewHolder<MovieState>).bind(state)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) VIEW_TYPE_DATA else VIEW_TYPE_LOADING
    }

    fun setNetworkState(newState: MovieState) {
        val previousState: MovieState? = this.state
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
        return state !== MovieState.HidePageLoading
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {
            if (movie == null) return

            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
            val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
            val poster = view.findViewById<ImageView>(R.id.poster)
            val tvRating = view.findViewById<TextView>(R.id.tvRating)
            val addToFav = view.findViewById<ImageView>(R.id.ivWatchlist)

            addToFav.visibility = View.GONE

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
                itemClickListener?.itemClick(adapterPosition, movie)
            }
        }
    }
}
