package com.feryaeldev.djexperience.ui.common

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.Artist
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", item.id)
            val navController = Navigation.findNavController(it)
            if (navController.currentDestination?.id == R.id.profileFragment) {
                navController.navigate(R.id.action_profileFragment_to_artistDetailsFragment, bundle)
            } else if (navController.currentDestination?.id == R.id.searchFragment) {
                navController.navigate(R.id.action_searchFragment_to_artistDetailsFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int = artists.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //private val context: Context = view.context
        //private val layout: LinearLayout = view.findViewById(R.id.search_item)
        private val artistUsername: TextView = view.findViewById(R.id.searchitem_username)
        private val artistImage: CircleImageView = view.findViewById(R.id.searchitem_image)

        fun render(item: Artist) {
            val id = item.id
            val profilePicRef =
                Firebase.storage.reference.child("profile_images/${id}.jpg")
            val tempFile = File.createTempFile("tempImage", "jpg")
            profilePicRef.getFile(tempFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                artistImage.setImageBitmap(bitmap)
            }
            tempFile.delete()

            // Por si solo viene el artist con el ID y no rellenado.
            id?.let {
                Firebase.firestore.collection("artists").document(id).get()
                    .addOnSuccessListener { documentSnap ->
                        val artist = Artist(
                            documentSnap["id"].toString(),
                            documentSnap["name"].toString(),
                            documentSnap["surnames"].toString(),
                            documentSnap["username"].toString(),
                            documentSnap["email"].toString(),
                            documentSnap["country"].toString(),
                            documentSnap["category"].toString(),
                            documentSnap["age"].toString().toLong(),
                            documentSnap["website"].toString()
                        )
                        artistUsername.text = artist.username
                    }
            }

            /*
            layout.setOnClickListener {
                val fragment = ArtistDetailsFragment()
                val bundle = Bundle()
                bundle.putString("id", id)
                fragment.arguments = bundle
                (context as MainActivity).replaceFragment(fragment)
            }
            */
        }
    }
}