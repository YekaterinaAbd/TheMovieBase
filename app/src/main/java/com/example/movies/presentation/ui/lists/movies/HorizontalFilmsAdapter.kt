package com.example.movies.presentation.ui.lists.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.network.IMAGE_URL
import com.example.movies.domain.model.Movie
import com.example.movies.presentation.utils.extensions.Side
import com.example.movies.presentation.utils.extensions.setMargin
import com.squareup.picasso.Picasso

interface SimpleItemClickListener {
    fun itemClick(position: Int, item: Movie)
}

class HorizontalFilmsAdapter(
    private val simpleItemClickListener: SimpleItemClickListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movies = mutableListOf<Movie>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.film_small_view, parent, false)
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

    inner class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(movie: Movie?) {

            val view = view.findViewById<ConstraintLayout>(R.id.layout)
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val ivPoster = view.findViewById<ImageView>(R.id.poster)

            if (movie != null) {

                if (adapterPosition == 0) itemView.setMargin(16, Side.END)

                tvTitle.text = movie.title

                Picasso.get()
                    .load(IMAGE_URL + movie.posterPath)
                    .into(ivPoster)

                view.setOnClickListener {
                    simpleItemClickListener?.itemClick(adapterPosition, movie)
                }
            }
        }
    }
}