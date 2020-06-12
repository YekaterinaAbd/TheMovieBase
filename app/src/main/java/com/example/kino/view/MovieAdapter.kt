package com.example.kino.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.model.movie.Movie
import com.squareup.picasso.Picasso

open class MovieAdapter(
    private val itemClickListener: RecyclerViewItemClick? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = mutableListOf<Movie>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.film_object, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieAdapter.MovieViewHolder)
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
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }

    fun removeItem(movie: Movie) {
        movies.remove(movie)
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {

            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
            val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
            val poster = view.findViewById<ImageView>(R.id.ivPoster)
            val tvVotesCount = view.findViewById<TextView>(R.id.tvVotesCount)
            val tvRating = view.findViewById<TextView>(R.id.tvRating)
            val addToFav = view.findViewById<ImageView>(R.id.tvAddToFav)

            if (movie != null) {
                if (movie.isClicked) {
                    addToFav.setImageResource(R.drawable.ic_turned_in_black_24dp)
                } else {
                    addToFav.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
                }

                tvTitle.text = movie.title
                tvReleaseDate.text = movie.releaseDate.substring(0, 4)
                tvVotesCount.text = movie.voteCount.toString()
                tvRating.text = movie.voteAverage.toString()

                if (movie.genreNames.isNotEmpty())
                    tvGenres.text = movie.genreNames.substring(0, movie.genreNames.length - 2)
                else {
                    tvGenres.text = ""
                }

                Picasso.get()
                    .load("https://image.tmdb.org/t/p/w500" + movie.posterPath)
                    .into(poster)

                view.setOnClickListener {
                    itemClickListener?.itemClick(adapterPosition, movie)
                }

                addToFav.setOnClickListener {
                    itemClickListener?.addToFavourites(adapterPosition, movie)
                    if (movie.isClicked) {
                        addToFav.setImageResource(R.drawable.ic_turned_in_black_24dp)
                    } else {
                        addToFav.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
                    }
                }
            }
        }
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun addToFavourites(position: Int, item: Movie)
    }

}