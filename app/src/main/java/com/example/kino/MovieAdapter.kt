package com.example.kino


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.MovieClasses.Movie
import com.example.kino.MovieClasses.MovieResults
import com.squareup.picasso.Picasso

class MovieAdapter(
    var movies: List<Movie>? = null,
    val itemClickListener: RecyclerViewItemClick? = null
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.film_object, p0, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int = movies?.size ?: 0

    override fun onBindViewHolder(p0: MovieViewHolder, p1: Int) {
        p0.bind(movies?.get(p1))
    }

    fun clearAll() {
        (movies as? ArrayList<Movie>)?.clear()
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val tvPostId = view.findViewById<TextView>(R.id.tvPostId)
            val tvUserId = view.findViewById<TextView>(R.id.tvUserId)
            val poster = view.findViewById<ImageView>(R.id.poster)

            tvTitle.text = movie?.title
            tvPostId.text = movie?.id.toString()
            tvUserId.text = movie?.vote_average.toString()
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w500" + movie?.poster_path)
                .into(poster)


            view.setOnClickListener {
                itemClickListener?.itemClick(adapterPosition, movie!!)
            }
        }
    }

    interface RecyclerViewItemClick {

        fun itemClick(position: Int, item: Movie)
    }
}