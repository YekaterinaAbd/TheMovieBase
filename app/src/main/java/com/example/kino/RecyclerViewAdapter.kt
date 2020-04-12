package com.example.kino

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.MovieClasses.Movie
import com.squareup.picasso.Picasso
import java.util.*

class RecyclerViewAdapter(
    //TESTING8
    var movies: List<Movie>? = null,
    val itemClickListener: RecyclerViewItemClick? = null

) : RecyclerView.Adapter<RecyclerViewAdapter.MovieViewHolder>() {

    var moviePosition = 1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.film_object, p0, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(p0: MovieViewHolder, p1: Int) {
        p0.bind(movies?.get(p1))
    }

    fun clearAll() {
        moviePosition = 1
        (movies as? ArrayList<Movie>)?.clear()
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
            val number = view.findViewById<TextView>(R.id.number)
            val addToFav = view.findViewById<ImageView>(R.id.tvAddToFav)

            if (movie?.isClicked!!) {
                addToFav.setImageResource(R.drawable.ic_turned_in_black_24dp)
            } else {
                addToFav.setImageResource(R.drawable.ic_turned_in_not_black_24dp)
            }

            if (movie.position == 0) {
                movie.position = moviePosition
                moviePosition++
            }

            tvTitle.text = movie.title
            tvReleaseDate.text = movie.releaseDate.substring(0, 4)
            tvVotesCount.text = movie.voteCount.toString()
            tvRating.text = movie.voteAverage.toString()
            number.text = movie.position.toString()
            tvGenres.text = ""

            if (movie.genreNames.isNotEmpty()) {
                for (i in 0 until movie.genreNames.size) {
                    if (i <= 3) {
                        if (i == 0) tvGenres.text = movie.genreNames[i].toLowerCase(Locale.ROOT)
                        else tvGenres.append(", ${movie.genreNames[i].toLowerCase(Locale.ROOT)}")
                    }
                }
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

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
        fun addToFavourites(position: Int, item: Movie)
    }
}