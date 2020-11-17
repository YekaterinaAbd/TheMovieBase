package com.example.kino.presentation.utils

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.kino.R
import com.example.kino.presentation.ui.MovieState

class LoadMoreItemViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    fun bind(state: T?) {
        if (state == null) {
            progressBar.visibility = View.GONE
        }
        if (state == MovieState.ShowPageLoading) {
            progressBar.visibility = View.VISIBLE
        } else if (state == MovieState.HidePageLoading) {
            progressBar.visibility = View.GONE
        }
    }
}