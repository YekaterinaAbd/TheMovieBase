package com.example.kino.presentation.ui.lists.top

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.data.network.IMAGE_URL
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.utils.Margin
import com.example.kino.presentation.utils.Side
import com.squareup.picasso.Picasso

interface ItemClickListener {
    fun itemClick(item: Movie)
    fun addToFavourites(item: Movie)
    fun addToWatchlist(item: Movie)
}

class TopAdapter(
    private val itemClickListener: ItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    private var moviePosition = 1
    private var movies = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                MovieViewHolder(
                    inflater.inflate(R.layout.film_object_view, parent, false)
                )
            }
            VIEW_TYPE_LOADING -> LoaderViewHolder(
                inflater.inflate(R.layout.loader, parent, false)
            )
            else -> throw Throwable("invalid view")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == movies.size - 1) VIEW_TYPE_LOADING
            else VIEW_TYPE_NORMAL
        } else VIEW_TYPE_NORMAL
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder) holder.bind(movies[position])
    }

    fun clearAll() {
        movies.clear()
        moviePosition = 1
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        movies.add(Movie(id = -1))
        notifyItemInserted(movies.size.minus(1))
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = movies.size.minus(1)
        if (movies.isNotEmpty()) {
            val item = getItem(position)
            if (item != null) {
                movies.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun getItem(position: Int): Movie? {
        return movies[position]
    }

    fun addItems(moviesList: List<Movie>) {
        if (movies.size == 0) movies = moviesList as MutableList<Movie>
        else {
            if (movies[movies.size - 1] != moviesList[moviesList.size - 1])
                movies.addAll(moviesList)
        }
        notifyDataSetChanged()
    }

    fun replaceItems(moviesList: List<Movie>) {
        isLoaderVisible = false
        movies = moviesList as MutableList<Movie>
        moviePosition = 1
        notifyDataSetChanged()
    }

    fun updateItem(movie: Movie) {
        val id = movie.id
        val isClicked = movie.isFavourite
        val foundMovie = movies.find { it.id == id }
        foundMovie?.isFavourite = isClicked
        notifyDataSetChanged()
    }

    fun removeItem(movie: Movie, position: Int) {
        val id = movie.id
        val foundMovie = movies.find { it.id == id }
        if (foundMovie != null) {
            movies.remove(foundMovie)
        }
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvReleaseDate: TextView = view.findViewById(R.id.tvReleaseDate)
        private val tvGenres: TextView = view.findViewById(R.id.tvGenres)
        private val poster: ImageView = view.findViewById(R.id.ivPoster)
        private val tvRating: TextView = view.findViewById(R.id.tvRating)
        private val number: TextView = view.findViewById(R.id.number)
        private val addToFav: ImageView = view.findViewById(R.id.ivLike)
        private val addToWatchlist: ImageView = view.findViewById(R.id.ivWatchlist)

        fun bind(movie: Movie?) {

            addToFav.visibility = View.VISIBLE
            addToWatchlist.visibility = View.VISIBLE

            if (adapterPosition == 0) Margin.setMargin(16, itemView.context, view, Side.TOP)

            if (movie != null) {

                if (movie.isFavourite) addToFav.setImageResource(R.drawable.ic_favourite)
                else addToFav.setImageResource(R.drawable.ic_favourite_border)

                if (movie.isInWatchList) addToWatchlist.setImageResource(R.drawable.ic_watchlist_filled)
                else addToWatchlist.setImageResource(R.drawable.ic_watchlist)

                if (movie.position == 0) {
                    movie.position = moviePosition
                    moviePosition++
                }

                tvTitle.text = movie.title
                if (!movie.releaseDate.isNullOrEmpty())
                    tvReleaseDate.text = movie.releaseDate?.substring(0, 4)
                tvRating.text = movie.voteAverage.toString()
                tvGenres.text = movie.genreNames

                number.text = movie.position.toString()

                if (!movie.posterPath.isNullOrEmpty())
                    Picasso.get()
                        .load(IMAGE_URL + movie.posterPath)
                        .into(poster)

                view.setOnClickListener {
                    itemClickListener?.itemClick(movie)
                }

                addToFav.setOnClickListener {
                    itemClickListener?.addToFavourites(movie)
                    if (movie.isFavourite) addToFav.setImageResource(R.drawable.ic_favourite)
                    else addToFav.setImageResource(R.drawable.ic_favourite_border)
                }

                addToWatchlist.setOnClickListener {
                    itemClickListener?.addToWatchlist(movie)
                    if (movie.isInWatchList) addToWatchlist.setImageResource(R.drawable.ic_watchlist_filled)
                    else addToWatchlist.setImageResource(R.drawable.ic_watchlist)
                }
            }
        }
    }

    class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
