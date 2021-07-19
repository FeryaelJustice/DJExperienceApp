package com.feryaeldev.djexperience.fragments.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.models.Artist
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SearchRecyclerViewAdapter(private val artists: MutableList<Artist>) :
    RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = artists[position]
        holder.render(item)
    }

    override fun getItemCount(): Int = artists.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val artistNickname = view.findViewById<TextView>(R.id.searchitem_nickname)
        val artistImage = view.findViewById<CircleImageView>(R.id.searchitem_image)

        fun render(item: Artist) {
            artistNickname.text = item.nickname
            val userId = item.id
            val profileRef =
                Firebase.storage.reference.child("artists/${userId}/profilepicture.jpg")
            profileRef.downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).placeholder(R.drawable.ic_baseline_person_24)
                    .error(R.drawable.ic_baseline_person_24).into(artistImage)
            }
        }
    }
}