package com.example.movies.presentation.ui.lists.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.data.model.entities.SearchQuery

interface OnQueryClickListener {
    fun onQueryClick(item: SearchQuery)
    fun onQueryRemove(item: SearchQuery)
}

class SearchHistoryAdapter(
    val onQueryClickListener: OnQueryClickListener
) :
    RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryViewHolder>() {

    private var queries = mutableListOf<SearchQuery>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.query_object_view, parent, false)
        return SearchHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(queries[position])
    }

    override fun getItemCount(): Int {
        return queries.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addItems(items: List<SearchQuery>?) {
        if (!items.isNullOrEmpty()) {
            queries = items as MutableList<SearchQuery>
            notifyDataSetChanged()
        }
    }

    fun addItem(item: SearchQuery?) {
        if (item != null) {
            queries.add(item)
            notifyItemInserted(itemCount - 1)
        }
    }

    fun removeItem(item: SearchQuery?) {
        if (item != null) {
            val index = queries.indexOf(item)
            queries.remove(item)
            notifyItemRemoved(index)
        }
    }

    fun removeAll() {
        queries.clear()
        notifyDataSetChanged()
    }

    inner class SearchHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivRemove: ImageView = itemView.findViewById(R.id.ivRemove)
        private val tvQuery: TextView = itemView.findViewById(R.id.tvQuery)

        fun bind(query: SearchQuery) {
            tvQuery.text = query.query

            tvQuery.setOnClickListener {
                onQueryClickListener.onQueryClick(query)
            }

            ivRemove.setOnClickListener {
                removeItem(query)
                onQueryClickListener.onQueryRemove(query)
            }
        }
    }
}
