package com.feryaeldev.djexperience.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.model.domain.New

class NewsRecyclerViewAdapter(private val news: MutableList<New>): RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = news[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = news.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun render(item: New) {

        }
    }
}