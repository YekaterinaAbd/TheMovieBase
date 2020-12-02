package com.example.kino.presentation.ui.lists.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.data.network.IMAGE_URL
import com.example.kino.domain.model.Movie
import com.example.kino.presentation.utils.Margin
import com.example.kino.presentation.utils.Side
import com.squareup.picasso.Picasso

class HorizontalFilmsAdapter(
    private val itemClickListener: ItemClickListener? = null
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
            val ivPoster = view.findViewById<ImageView>(R.id.ivPoster)

            if (movie != null) {

                if (adapterPosition == 0) Margin.setMargin(16, itemView.context, view, Side.LEFT)

                tvTitle.text = movie.title

                Picasso.get()
                    .load(IMAGE_URL + movie.posterPath)
                    .into(ivPoster)

                view.setOnClickListener {
                    itemClickListener?.itemClick(adapterPosition, movie)
                }
            }
        }
    }

    interface ItemClickListener {
        fun itemClick(position: Int, item: Movie)
    }
}