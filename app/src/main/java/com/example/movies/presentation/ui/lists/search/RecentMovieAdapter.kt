package com.example.movies.presentation.ui.lists.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.model.entities.RecentMovie
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.presentation.ui.lists.movies.SimpleItemClickListener
import com.squareup.picasso.Picasso

class RecentMovieAdapter(
    private val itemClickListener: SimpleItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = mutableListOf<RecentMovie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recent_movie_view, parent, false)
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

    fun addItems(moviesList: List<RecentMovie>) {
        movies = moviesList as MutableList<RecentMovie>
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: RecentMovie?) {
            if (movie == null) return

            val view = view.findViewById<CardView>(R.id.layout)
            val title = view.findViewById<TextView>(R.id.title)
            val poster = view.findViewById<ImageView>(R.id.poster)

            title.text = movie.title

            Picasso.get()
                .load(IMAGE_URL + movie.posterPath)
                .into(poster)

            view.setOnClickListener {
                itemClickListener.itemClick(movie.id)
            }
        }
    }
}
