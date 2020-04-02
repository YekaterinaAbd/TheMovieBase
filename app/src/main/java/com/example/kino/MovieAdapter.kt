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
import kotlin.collections.ArrayList

class MovieAdapter(
    var movies: List<Movie>? = null,
    val itemClickListener: RecyclerViewItemClick? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    var num = 1

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.film_object, p0, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies?.size ?: 0

    override fun onBindViewHolder(p0: MovieViewHolder, p1: Int) {
        p0.bind(movies?.get(p1))
    }

    fun clearAll() {
        num = 1
        (movies as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvReleaseDate = view.findViewById<TextView>(R.id.tvReleaseDate)
            val tvGenres = view.findViewById<TextView>(R.id.tvGenres)
            val poster = view.findViewById<ImageView>(R.id.poster)
            val tvVotesCount = view.findViewById<TextView>(R.id.tvVotesCount)
            val tvRating = view.findViewById<TextView>(R.id.tvRating)
            val number = view.findViewById<TextView>(R.id.number)
           // val popularity = view.findViewById<TextView>(R.id.tvPopularity)

            if(movie?.number == 0){
            movie.number = num
            num++}

            tvTitle.text = movie?.title
            tvReleaseDate.text = movie?.release_date?.substring(0, 4).toString()
            tvVotesCount.text = movie?.vote_count.toString()
            tvRating.text = movie?.vote_average.toString()
            number.text = movie?.number.toString()
            tvGenres.text = ""
            for(i in 0 until movie?.genre_names!!.size){
                if(i <= 3) {
                    if(i == 0) tvGenres.text = movie.genre_names[i].toLowerCase(Locale.ROOT)
                    else tvGenres.append(", ${movie.genre_names[i].toLowerCase(Locale.ROOT)}")
                }
            }
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie.poster_path)
                .into(poster)

            view.setOnClickListener {
                itemClickListener?.itemClick(adapterPosition, movie)
            }
        }
    }

    interface RecyclerViewItemClick {
        fun itemClick(position: Int, item: Movie)
    }
}