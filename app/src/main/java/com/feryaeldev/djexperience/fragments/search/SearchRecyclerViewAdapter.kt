package com.feryaeldev.djexperience.fragments.search

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.models.Artist
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

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
        private val artistNickname: TextView = view.findViewById(R.id.searchitem_nickname)
        private val artistImage: CircleImageView = view.findViewById(R.id.searchitem_image)

        fun render(item: Artist) {
            artistNickname.text = item.nickname
            val userId = item.id
            val profilePicRef =
                FirebaseStorage.getInstance().reference.child("profile_images/${userId}.jpg")
            val tempFile = File.createTempFile("tempImage", "jpg")
            profilePicRef.getFile(tempFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                artistImage.setImageBitmap(bitmap)
            }
            tempFile.delete()
        }
    }
}