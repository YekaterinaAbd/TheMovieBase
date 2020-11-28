package com.example.kino.presentation.ui.lists.favouries

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.data.network.IMAGE_URL
import com.example.kino.domain.model.Movie
import com.squareup.picasso.Picasso

interface ItemClickListener {
    fun itemClick(item: Movie)
    fun addToFavourites(item: Movie)
    fun addToWatchlist(item: Movie)
}

class FavouritesAdapter(
    private val itemClickListener: ItemClickListener? = null,
    private val isWatchlist: Boolean = false
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = mutableListOf<Movie>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.film_object_view, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder)
            holder.bind(movies[position])
    }

    fun clearAll() {
        movies.clear()
        notifyDataSetChanged()
    }

    fun addItems(moviesList: List<Movie>) {
        movies = moviesList as MutableList<Movie>
        notifyDataSetChanged()
    }

    fun addItem(movie: Movie) {
        if (!movies.contains(movie)) {
            movies.add(movie)
            notifyItemInserted(movies.size - 1)
        }
    }

    fun removeItem(movie: Movie) {
        val id = movie.id
        val foundMovie = movies.find { it.id == id }
        if (foundMovie != null) {
            movies.remove(foundMovie)
        }
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private fun setMargin() {
            val sizeInDP = 16

            val marginInDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), itemView.context.resources
                    .displayMetrics
            ).toInt()

            val params: ViewGroup.MarginLayoutParams =
                view.layoutParams as ViewGroup.MarginLayoutParams
            params.topMargin = marginInDp
        }

        fun bind(movie: Movie?) {

            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
            val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
            val poster = view.findViewById<ImageView>(R.id.ivPoster)
            val tvRating = view.findViewById<TextView>(R.id.tvRating)
            val addToFav = view.findViewById<ImageView>(R.id.ivLike)
            val addToWatchlist = view.findViewById<ImageView>(R.id.ivWatchlist)

            if (movie != null) {
                if (adapterPosition == 0) setMargin()

                tvTitle.text = movie.title
                tvReleaseDate.text = movie.releaseDate?.substring(0, 4)
                tvRating.text = movie.voteAverage.toString()
                tvGenres.text = movie.genreNames

                Picasso.get()
                    .load(IMAGE_URL + movie.posterPath)
                    .into(poster)

                view.setOnClickListener {
                    itemClickListener?.itemClick(movie)
                }

                if (isWatchlist) {
                    addToWatchlist.visibility = View.VISIBLE
                    addToWatchlist.setImageResource(R.drawable.ic_watchlist_filled)

                    addToWatchlist.setOnClickListener {
                        itemClickListener?.addToWatchlist(movie)
                        if (movie.isInWatchList) addToWatchlist.setImageResource(R.drawable.ic_watchlist_filled)
                        else removeItem(movie)
                    }

                } else {
                    addToFav.visibility = View.VISIBLE
                    addToFav.setImageResource(R.drawable.ic_favourite)

                    addToFav.setOnClickListener {
                        itemClickListener?.addToFavourites(movie)
                        if (movie.isFavourite) addToFav.setImageResource(R.drawable.ic_favourite)
                        else removeItem(movie)
                    }
                }
            }
        }
    }
}
