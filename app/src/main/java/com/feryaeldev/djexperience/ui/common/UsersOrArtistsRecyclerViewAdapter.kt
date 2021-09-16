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
import com.feryaeldev.djexperience.data.model.domain.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class UsersOrArtistsRecyclerViewAdapter(private val users: MutableList<User>) :
    RecyclerView.Adapter<UsersOrArtistsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.render(item)
        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("id", item.id)
            val navController = Navigation.findNavController(it)
            if (navController.currentDestination?.id == R.id.profileFragment) {
                navController.navigate(R.id.action_profileFragment_to_detailsFragment, bundle)
            } else if (navController.currentDestination?.id == R.id.searchFragment) {
                navController.navigate(R.id.action_searchFragment_to_detailsFragment, bundle)
            }
        }
    }

    override fun getItemCount(): Int = users.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val artistUsername: TextView = view.findViewById(R.id.searchitem_username)
        private val artistImage: CircleImageView = view.findViewById(R.id.searchitem_image)

        fun render(user: User) {
            val db = Firebase.firestore

            val profilePicRef =
                Firebase.storage.reference.child("profile_images/${user.id}.jpg")
            val tempFile = File.createTempFile("tempImage", "jpg")
            profilePicRef.getFile(tempFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                artistImage.setImageBitmap(bitmap)
            }
            tempFile.delete()

            user.id?.let { id ->
                db.collection("artists").document(id).get()
                    .addOnSuccessListener { documentSnap ->
                        // Although it doesnt find anything, it doesnt go to On Failure Listener, we have to check a field if its not null
                        val usernameRes = documentSnap.data?.get("username").toString()
                        if (documentSnap.data?.get("id") != null) {
                            artistUsername.text = usernameRes
                        } else {
                            db.collection("users").document(id).get()
                                .addOnSuccessListener { docSnap ->
                                    val usernameUserRes = docSnap.data?.get("username").toString()
                                    artistUsername.text = usernameUserRes
                                }
                        }
                    }
            }
        }
    }
}