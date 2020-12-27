package com.example.movies.presentation.ui.lists.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.domain.model.Movie
import com.example.movies.domain.model.MoviesType
import com.example.movies.domain.model.MoviesType.*
import com.squareup.picasso.Picasso

private const val VIEW_TYPE_LOADING = 0
private const val VIEW_TYPE_NORMAL = 1

class ListsAdapter(
    private val itemClickListener: ItemClickListener? = null,
    private val moviesType: MoviesType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoaderVisible = false

    private var movies = ArrayList<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.film_object_view, parent, false)
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                if (moviesType == WATCH_LIST || moviesType == FAVOURITES || moviesType == RATED)
                    MyMovieViewHolder(view)
                else
                    MovieViewHolder(view)
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
        if (holder is MyMovieViewHolder) holder.bind(movies[position])
    }

    fun clearAll() {
        movies.clear()
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        movies.add(Movie(id = -1))
        notifyItemInserted(movies.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position = movies.size - 1
        if (movies.isNotEmpty()) {
            val item = getItem(position)
            movies.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): Movie {
        return movies[position]
    }

    fun addItems(moviesList: List<Movie>) {
        movies.addAll(moviesList)
        notifyDataSetChanged()
    }

    fun replaceItems(moviesList: List<Movie>) {
        isLoaderVisible = false
        movies = moviesList as ArrayList<Movie>
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
        private val poster: ImageView = view.findViewById(R.id.poster)
        private val tvRating: TextView = view.findViewById(R.id.tvRating)
        private val verticalDots: ImageView = view.findViewById(R.id.verticalDots)

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
                itemClickListener?.itemClick(movie)
            }

            verticalDots.setOnClickListener {
                itemClickListener?.openActionsDialog(movie)
            }
        }
    }

    inner class MyMovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvReleaseDate: TextView = view.findViewById(R.id.tvReleaseDate)
        private val tvGenres: TextView = view.findViewById(R.id.tvGenres)
        private val poster: ImageView = view.findViewById(R.id.poster)
        private val tvRating: TextView = view.findViewById(R.id.tvRating)
        private val addToFav: ImageView = view.findViewById(R.id.ivLike)
        private val addToWatchlist: ImageView = view.findViewById(R.id.ivWatchlist)
        private val tvMyRating: TextView = view.findViewById(R.id.tvRated)

        fun bind(movie: Movie?) {
            if (movie == null) return

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

            when (moviesType) {
                WATCH_LIST -> {
                    addToWatchlist.visibility = View.VISIBLE
                    addToWatchlist.setImageResource(R.drawable.ic_watchlist_filled)

                    addToWatchlist.setOnClickListener {
                        itemClickListener?.addToWatchlist(movie)
                        removeItem(movie, adapterPosition)
                    }
                }
                FAVOURITES -> {
                    addToFav.visibility = View.VISIBLE
                    addToFav.setImageResource(R.drawable.ic_favourite)

                    addToFav.setOnClickListener {
                        itemClickListener?.addToFavourites(movie)
                        removeItem(movie, adapterPosition)
                    }
                }
                else -> {
                    tvMyRating.visibility = View.VISIBLE
                    tvMyRating.text =
                        itemView.context.getString(R.string.my_rating, movie.rating?.toFloat())
                }
            }
        }
    }

    inner class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface ItemClickListener {
        fun itemClick(item: Movie)
        fun addToFavourites(item: Movie)
        fun addToWatchlist(item: Movie)
        fun openActionsDialog(item: Movie)
    }
}
