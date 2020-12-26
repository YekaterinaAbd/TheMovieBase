package com.example.movies.presentation.ui.movie_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.model.movie.Cast
import com.example.movies.data.network.IMAGE_URL
import com.squareup.picasso.Picasso

class CastAdapter() : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    private var cast: List<Cast> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cast_view, parent, false)
        return CastViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(cast[position])
    }

    override fun getItemCount(): Int {
        return cast.size
    }

    fun addAll(cast: List<Cast>) {
        this.cast = cast
    }

    inner class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val image = view.findViewById<ImageView>(R.id.image)
        private val name = view.findViewById<TextView>(R.id.name)
        private val character = view.findViewById<TextView>(R.id.character)

        fun bind(cast: Cast?) {
            if (cast == null) return
            name.text = cast.name
            character.text = cast.character
            Picasso.get().load(IMAGE_URL + cast.profilePath).into(image)
        }
    }
}