package com.feryaeldev.djexperience.fragments.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.common.ArtistsRecyclerViewAdapter
import com.feryaeldev.djexperience.data.models.Artist
import com.feryaeldev.djexperience.data.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class ProfileFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ArtistsRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        var followingArtistsIds: ArrayList<String>

        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid
        val docRef = userId?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val nickname: TextView = view.findViewById(R.id.profile_nickname)
                val category: TextView = view.findViewById(R.id.profile_category)
                val country: TextView = view.findViewById(R.id.profile_country)
                val user: User? = document.toObject(User::class.java)
                nickname.text = user?.nickname
                category.text = user?.category
                country.text = user?.country
                followingArtistsIds = user?.following ?: arrayListOf()

                mRecyclerView = view.findViewById(R.id.fragment_profile_recyclerView)
                mRecyclerView.layoutManager = GridLayoutManager(view.context, 3)

                val artistsList: MutableList<Artist> = arrayListOf()

                for (id: String in followingArtistsIds) {
                    artistsList.add(Artist(id, "", "", "", "", "", "", 0, ""))
                }

                mAdapter = ArtistsRecyclerViewAdapter(artistsList)
                mRecyclerView.adapter = mAdapter
                //mAdapter.notifyDataSetChanged()
            } else {
                Log.d("nodata", "No such document")
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "get failed with ", exception)
        }
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${userId}.jpg")

        val image: ImageView = view.findViewById(R.id.profile_photo)
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        /*
        profilePicRef.downloadUrl.addOnSuccessListener {
            val image: ImageView = view.findViewById(R.id.profile_photo)
            Picasso.get().load(it)
                .error(R.drawable.ic_baseline_person_24).into(image)
        }
        */

        view.findViewById<Button>(R.id.profile_editProfileBtn).setOnClickListener {
            //startActivity(Intent(view.context, EditProfileFragment::class.java))
            findNavController().navigate(
                R.id.action_profileFragment_to_editProfileFragment,
                arguments
            )
        }


        return view
    }
}