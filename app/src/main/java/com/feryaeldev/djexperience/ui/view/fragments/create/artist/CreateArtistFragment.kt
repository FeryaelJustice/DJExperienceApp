package com.feryaeldev.djexperience.ui.view.fragments.create.artist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.util.asMap
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class CreateArtistFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_artist, container, false)

        var uri = Uri.parse("")

        val image: ImageView = view.findViewById(R.id.createArtist_photo)
        val email: EditText = view.findViewById(R.id.createArtist_email)
        val username: EditText = view.findViewById(R.id.createArtist_username)
        val name: EditText = view.findViewById(R.id.createArtist_name)
        val surnames: EditText = view.findViewById(R.id.createArtist_surnames)
        val country: EditText = view.findViewById(R.id.createArtist_country)
        val age: EditText = view.findViewById(R.id.createArtist_age)
        val website: EditText = view.findViewById(R.id.createArtist_website)

        // Profile picture
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { url ->
                        uri = url
                        Picasso.get().load(uri).into(image)
                    }
                }
            }
        image.setOnClickListener {
            val intentPick = Intent(Intent.ACTION_PICK)
            intentPick.type = "image/*"
            resultLauncher.launch(intentPick)
        }

        val db = Firebase.firestore
        val artistsDocRef = db.collection("artists")
        val profilePicsRef =
            Firebase.storage.reference

        view.findViewById<Button>(R.id.createArtist_saveBtn).setOnClickListener {
            if (username.text.toString().isNotBlank() && email.text.toString()
                    .isNotBlank() && name.text.toString()
                    .isNotBlank() && surnames.text.toString()
                    .isNotBlank() && country.text.toString().isNotBlank() && age.text.toString()
                    .isNotBlank() && website.text.toString().isNotBlank()
            ) {
                val user = User()
                user.id = randomString(28)
                user.email = email.text.toString()
                user.username = username.text.toString()
                user.name = name.text.toString()
                user.surnames = surnames.text.toString()
                user.country = country.text.toString()
                user.category = "Artist"
                user.age = age.text.toString().toLongOrNull()
                user.website = website.text.toString()

                profilePicsRef.child("profile_images/${user.id}.jpg").putFile(uri)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            artistsDocRef.document(user.id!!).set(user.asMap())
                                .addOnSuccessListener {
                                    showMessageLong("Artist created!")
                                    findNavController().popBackStack()
                                }.addOnFailureListener {
                                    showMessageShort("Failed!")
                                }
                        } else {
                            showMessageLong("Failed! Error on uploading image...")
                        }
                    }.addOnFailureListener {
                        showMessageLong("Failed! Error on uploading image...")
                    }
            } else {
                Toast.makeText(
                    view.context,
                    "At least one of the fields is empty!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_createArtist_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    fun randomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }
}