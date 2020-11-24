package com.example.kino.presentation.ui.lists.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.data.network.IMAGE_URL
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.ui.MovieState
import com.example.kino.presentation.utils.LoadMoreItemViewHolder
import com.squareup.picasso.Picasso

class PaginationAdapter(
    private val itemClickListener: RecyclerViewItemClick? = null
) : PagedListAdapter<Movie, RecyclerView.ViewHolder>(DiffUtilCallBack) {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_DATA = 1

    private var state: MovieState = MovieState.ShowPageLoading

    companion object {
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
        return state != null && state !== MovieState.HidePageLoading
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {

            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
            val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
            val poster = view.findViewById<ImageView>(R.id.ivPoster)
            val tvVotesCount = view.findViewById<TextView>(R.id.tvVotesCount)
            val tvRating = view.findViewById<TextView>(R.id.tvRating)
            val number = view.findViewById<TextView>(R.id.number)
            val addToFav = view.findViewById<ImageView>(R.id.tvAddToFav)

            addToFav.visibility = View.GONE

            if (movie != null) {
//                if (movie.isClicked) {
//                    addToFav.setImageResource(R.drawable.ic_turned_in_black_24dp)
//                } else {
//                    addToFav.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
//                }

                tvTitle.text = movie.title
                if (!movie.releaseDate.isNullOrEmpty())
                    tvReleaseDate.text = movie.releaseDate?.substring(0, 4)
                tvVotesCount.text = movie.voteCount.toString()
                tvRating.text = movie.voteAverage.toString()
                tvGenres.text = movie.genreNames

                if (!movie.posterPath.isNullOrEmpty())
                    Picasso.get()
                        .load(IMAGE_URL + movie.posterPath)
                        .into(poster)

                view.setOnClickListener {
                    itemClickListener?.itemClick(adapterPosition, movie)
                }

//                addToFav.setOnClickListener {
//                    itemClickListener?.addToFavourites(adapterPosition, movie)
//                    if (movie.isClicked) {
//                        addToFav.setImageResource(R.drawable.ic_turned_in_black_24dp)
//                    } else {
//                        addToFav.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
//                    }
//                }
            }
        }
    }


    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun addToFavourites(position: Int, item: Movie)
    }
}
