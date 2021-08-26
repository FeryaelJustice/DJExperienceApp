package com.feryaeldev.djexperience.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.New

class NewsRecyclerViewAdapter(private val news: MutableList<New>): RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.search_new_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = news[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = news.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val authorTv: TextView = view.findViewById(R.id.searchNewItem_author)
        private val titleTv: TextView = view.findViewById(R.id.searchNewItem_title)
        private val descriptionTv: TextView = view.findViewById(R.id.searchNewItem_description)

        fun render(item: New) {
            authorTv.text = itemView.context.getString(R.string.author,item.author)
            titleTv.text = itemView.context.getString(R.string.title,item.title)
            descriptionTv.text = itemView.context.getString(R.string.description,item.description)
        }
    }
}