package com.feryaeldev.djexperience.common

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.activities.MainActivity
import com.feryaeldev.djexperience.data.models.Artist
import com.feryaeldev.djexperience.fragments.artistdetails.ArtistDetailsFragment
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class ArtistsRecyclerViewAdapter(private val artists: MutableList<Artist>) :
    RecyclerView.Adapter<ArtistsRecyclerViewAdapter.ViewHolder>() {

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
        private val context: Context = view.context
        private val layout: LinearLayout = view.findViewById(R.id.search_item)
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
            layout.setOnClickListener {
                val fragment = ArtistDetailsFragment()
                val bundle = Bundle()
                bundle.putString("id", userId)
                fragment.arguments = bundle
                (context as MainActivity).replaceFragment(fragment)
            }
        }
    }
}