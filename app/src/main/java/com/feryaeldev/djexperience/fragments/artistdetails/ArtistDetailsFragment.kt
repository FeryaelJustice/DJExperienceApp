package com.feryaeldev.djexperience.fragments.artistdetails

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.data.models.Artist
import com.feryaeldev.djexperience.data.models.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ArtistDetailsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist_details, container, false)
        view.findViewById<ImageView>(R.id.fragment_artist_details_close).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // VITAL VARIABLES
        val arguments = arguments
        val userId = Firebase.auth.currentUser?.uid
        val artistId = arguments?.getString("id")

        val addRemoveToProfile: FloatingActionButton =
            view.findViewById(R.id.fragment_artist_details_addRemoveToProfile)
        val addRemoveToProfileText: TextView =
            view.findViewById(R.id.fragment_artist_details_addRemoveToProfileText)

        val image: ImageView = view.findViewById(R.id.fragment_artist_details_photo)
        val nickname: TextView = view.findViewById(R.id.fragment_artist_details_nickname)
        val name: TextView = view.findViewById(R.id.fragment_artist_details_name)
        val surnames: TextView = view.findViewById(R.id.fragment_artist_details_surnames)
        val country: TextView = view.findViewById(R.id.fragment_artist_details_country)
        val category: TextView = view.findViewById(R.id.fragment_artist_details_category)
        val age: TextView = view.findViewById(R.id.fragment_artist_details_age)
        var websiteUrl = ""

        val db = Firebase.firestore
        // Get artist data
        artistId?.let { id ->
            db.collection("artists").document(id).get().addOnSuccessListener { documentSnap ->
                val artist = Artist(
                    documentSnap["id"].toString(),
                    documentSnap["name"].toString(),
                    documentSnap["surnames"].toString(),
                    documentSnap["nickname"].toString(),
                    documentSnap["email"].toString(),
                    documentSnap["country"].toString(),
                    documentSnap["category"].toString(),
                    documentSnap["age"].toString().toInt(),
                    documentSnap["website"].toString()
                )
                nickname.text = artist.nickname
                name.text = artist.name
                surnames.text = artist.surnames
                country.text = artist.country
                category.text = artist.category
                age.text = resources.getString(R.string.edad, artist.age.toString())
                websiteUrl = artist.website
            }
            val profilePicRef =
                FirebaseStorage.getInstance().reference.child("profile_images/${id}.jpg")
            val tempFile = File.createTempFile("tempImage", "jpg")
            profilePicRef.getFile(tempFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                image.setImageBitmap(bitmap)
            }
            tempFile.delete()
        }

        val docRef = userId?.let { db.collection("users").document(it) }
        // Update button if following artist
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val user: User? = document.toObject(User::class.java)
                var found = false
                user?.following?.forEach {
                    if (it == artistId) {
                        found = true
                    }
                }
                if (found) {
                    addRemoveToProfile.setImageResource(R.drawable.ic_baseline_remove_24)
                    addRemoveToProfile.tag = R.drawable.ic_baseline_remove_24
                    addRemoveToProfileText.text = view.resources.getString(R.string.substract)
                } else {
                    addRemoveToProfile.setImageResource(R.drawable.ic_baseline_add_24)
                    addRemoveToProfile.tag = R.drawable.ic_baseline_add_24
                    addRemoveToProfileText.text = view.resources.getString(R.string.add)
                }
            }
        }

        // Image go to website
        image.setOnClickListener {
            if (websiteUrl != "") {
                try {
                    val intentURL = Intent(Intent.ACTION_VIEW)
                    intentURL.data = Uri.parse("http://$websiteUrl")
                    startActivity(intentURL)
                } catch (e: Exception) {
                    showMessageShort("Error trying to open website of the artist: $e")
                }
            }
        }

        // Add or remove artist from following on user logic
        addRemoveToProfile.setOnClickListener {
            docRef?.get()?.addOnSuccessListener { document ->
                if (document != null) {
                    val user: User? = document.toObject(User::class.java)
                    // Push or substract artist
                    if (artistId != null) {
                        if (addRemoveToProfile.tag == R.drawable.ic_baseline_add_24) {
                            user?.following?.add(artistId)
                            addRemoveToProfile.setImageResource(R.drawable.ic_baseline_remove_24)
                            addRemoveToProfile.tag = R.drawable.ic_baseline_remove_24
                            addRemoveToProfileText.text =
                                view.resources.getString(R.string.substract)
                        } else {
                            val tempList = arrayListOf<String>()
                            user?.following?.let { it1 -> tempList.addAll(it1) }
                            user?.following?.forEach {
                                if (it == artistId) {
                                    tempList.remove(it)
                                }
                            }
                            user?.following = tempList
                            addRemoveToProfile.setImageResource(R.drawable.ic_baseline_add_24)
                            addRemoveToProfile.tag = R.drawable.ic_baseline_add_24
                            addRemoveToProfileText.text = view.resources.getString(R.string.add)
                        }
                    }
                    // Update
                    if (user != null) {
                        db.collection("users").document(userId).set(user)
                    }
                }
            }
        }

        return view
    }

}