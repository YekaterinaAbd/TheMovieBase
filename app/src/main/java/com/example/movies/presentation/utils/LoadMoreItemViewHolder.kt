package com.example.movies.presentation.utils

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.presentation.ui.LoadingState

class LoadMoreItemViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

    fun bind(state: T?) {
        if (state == null) {
            progressBar.visibility = View.GONE
        }
        if (state == LoadingState.ShowPageLoading) {
            progressBar.visibility = View.VISIBLE
        } else if (state == LoadingState.HidePageLoading) {
            progressBar.visibility = View.GONE
        }
    }
}

