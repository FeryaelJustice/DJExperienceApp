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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.lang.Exception

class ArtistDetailsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist_details, container, false)
        val arguments = arguments
        view.findViewById<ImageView>(R.id.fragment_artist_details_close).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        val userId = arguments?.getString("id")

        val image: ImageView = view.findViewById(R.id.fragment_artist_details_photo)
        val nickname: TextView = view.findViewById(R.id.fragment_artist_details_nickname)
        val name: TextView = view.findViewById(R.id.fragment_artist_details_name)
        val surnames: TextView = view.findViewById(R.id.fragment_artist_details_surnames)
        val country: TextView = view.findViewById(R.id.fragment_artist_details_country)
        val category: TextView = view.findViewById(R.id.fragment_artist_details_category)
        val age: TextView = view.findViewById(R.id.fragment_artist_details_age)
        var websiteUrl = ""

        val db = Firebase.firestore
        userId?.let { id ->
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

        image.setOnClickListener {
            if (websiteUrl != "") {
                try{
                    val intentURL = Intent(Intent.ACTION_VIEW)
                    intentURL.data = Uri.parse("http://$websiteUrl")
                    startActivity(intentURL)
                } catch (e: Exception){
                    showMessageShort("Error trying to open website of the artist: $e")
                }
            }
        }

        return view
    }

}