package com.feryaeldev.djexperience.ui.view.fragments.profile.edit

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.util.asMap
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File

class EditProfileFragment : BaseFragment() {

    private lateinit var user: User
    private lateinit var progressCircle: FragmentContainerView
    private lateinit var profileDataLayout: LinearLayoutCompat
    private var selectedCategory = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        user = User()

        profileDataLayout = view.findViewById(R.id.editprofile_data)
        progressCircle = view.findViewById(R.id.editprofile_fragmentProgressBar)

        profileDataLayout.visibility = View.GONE
        progressCircle.visibility = View.VISIBLE

        val image: ImageView = view.findViewById(R.id.editprofile_photo)
        val name: EditText = view.findViewById(R.id.editprofile_name)
        val surnames: EditText = view.findViewById(R.id.editprofile_surnames)
        val country: EditText = view.findViewById(R.id.editprofile_country)
        val age: EditText = view.findViewById(R.id.editprofile_age)
        val website: EditText = view.findViewById(R.id.editprofile_website)
        val categorySpinner: Spinner = view.findViewById(R.id.editprofile_category_sp)

        val userListTypes = resources.getStringArray(R.array.user_categories)
        val adapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, userListTypes)
        categorySpinner.adapter = adapter
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCategory = userListTypes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("todo", p0.toString())
            }
        }

        // Get current user or artist (we dont know if its user or artist here)
        val db = Firebase.firestore
        val userOrArtistID = Firebase.auth.currentUser?.uid

        val userDocRef = userOrArtistID?.let { db.collection("users").document(it) }
        val artistDocRef = userOrArtistID?.let { db.collection("artists").document(it) }
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/$userOrArtistID.jpg")

        // Search for the user or artist
        userDocRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                if (document.data?.size != null) {
                    // Get current user to edit
                    user = User(
                        document.data?.get("id").toString(),
                        document.data?.get("name").toString(),
                        document.data?.get("surnames").toString(),
                        document.data?.get("username").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("country").toString(),
                        document.data?.get("category").toString(),
                        document.data?.get("age").toString().toLongOrNull(),
                        document.data?.get("website").toString(),
                        arrayListOf()
                    )

                    name.setText(user.name)
                    surnames.setText(user.surnames)
                    country.setText(user.country)
                    age.setText(user.age.toString())
                    website.setText(user.website)
                    selectedCategory = user.category.toString()
                    if (user.category == "User") {
                        categorySpinner.setSelection(0)
                    } else {
                        categorySpinner.setSelection(1)
                    }

                    progressCircle.visibility = View.GONE
                    profileDataLayout.visibility = View.VISIBLE
                } else {
                    Log.d("error", "User no such document")

                    artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                        if (documentArtist != null) {
                            // Get current artist to edit
                            user = User(
                                documentArtist.data?.get("id").toString(),
                                documentArtist.data?.get("name").toString(),
                                documentArtist.data?.get("surnames").toString(),
                                documentArtist.data?.get("username").toString(),
                                documentArtist.data?.get("email").toString(),
                                documentArtist.data?.get("country").toString(),
                                documentArtist.data?.get("category").toString(),
                                documentArtist.data?.get("age").toString().toLongOrNull(),
                                documentArtist.data?.get("website").toString(),
                                arrayListOf()
                            )

                            name.setText(user.name)
                            surnames.setText(user.surnames)
                            country.setText(user.country)
                            age.setText(user.age.toString())
                            website.setText(user.website)
                            selectedCategory = user.category.toString()
                            if (user.category == "User") {
                                categorySpinner.setSelection(0)
                            } else {
                                categorySpinner.setSelection(1)
                            }

                            progressCircle.visibility = View.GONE
                            profileDataLayout.visibility = View.VISIBLE
                        } else {
                            Log.d("error", "Artist no such document")
                        }
                    }?.addOnFailureListener { exceptionArtist ->
                        Log.d("error", "Artist get failed with ", exceptionArtist)
                    }
                }
            } else {
                Log.d("error", "User no such document")

                artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                    if (documentArtist != null) {
                        // Get current artist to edit
                        user = User(
                            documentArtist.data?.get("id").toString(),
                            documentArtist.data?.get("name").toString(),
                            documentArtist.data?.get("surnames").toString(),
                            documentArtist.data?.get("username").toString(),
                            documentArtist.data?.get("email").toString(),
                            documentArtist.data?.get("country").toString(),
                            documentArtist.data?.get("category").toString(),
                            documentArtist.data?.get("age").toString().toLongOrNull(),
                            documentArtist.data?.get("website").toString(),
                            arrayListOf()
                        )

                        name.setText(user.name)
                        surnames.setText(user.surnames)
                        country.setText(user.country)
                        age.setText(user.age.toString())
                        website.setText(user.website)
                        selectedCategory = user.category.toString()
                        if (user.category == "User") {
                            categorySpinner.setSelection(0)
                        } else {
                            categorySpinner.setSelection(1)
                        }

                        progressCircle.visibility = View.GONE
                        profileDataLayout.visibility = View.VISIBLE
                    } else {
                        Log.d("error", "Artist no such document")
                    }
                }?.addOnFailureListener { exceptionArtist ->
                    Log.d("error", "Artist get failed with ", exceptionArtist)
                }
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "User get failed with ", exception)

            artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                if (documentArtist != null) {
                    // Get current artist to edit
                    user = User(
                        documentArtist.data?.get("id").toString(),
                        documentArtist.data?.get("name").toString(),
                        documentArtist.data?.get("surnames").toString(),
                        documentArtist.data?.get("username").toString(),
                        documentArtist.data?.get("email").toString(),
                        documentArtist.data?.get("country").toString(),
                        documentArtist.data?.get("category").toString(),
                        documentArtist.data?.get("age").toString().toLongOrNull(),
                        documentArtist.data?.get("website").toString(),
                        arrayListOf()
                    )

                    name.setText(user.name)
                    surnames.setText(user.surnames)
                    country.setText(user.country)
                    age.setText(user.age.toString())
                    website.setText(user.website)
                    selectedCategory = user.category.toString()
                    if (user.category == "User") {
                        categorySpinner.setSelection(0)
                    } else {
                        categorySpinner.setSelection(1)
                    }

                    progressCircle.visibility = View.GONE
                    profileDataLayout.visibility = View.VISIBLE
                } else {
                    Log.d("error", "Artist no such document")
                }
            }?.addOnFailureListener { exceptionArtist ->
                Log.d("error", "Artist get failed with ", exceptionArtist)
            }
        }

        // Get profile picture
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()


        // Save user profile picture
        // Dont put resultlauncher registerforactivityresult inside listeners cause fragment is not created (throws error)
        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    uri?.let { url ->
                        profilePicRef.putFile(url).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Picasso.get().load(url).into(image)
                                showMessageLong("Image uploaded successfully!")
                            } else {
                                showMessageLong("Error on uploading image...")
                            }
                        }
                    }
                }
            }
        image.setOnClickListener {
            val intentPick = Intent(Intent.ACTION_PICK)
            intentPick.type = "image/*"
            resultLauncher.launch(intentPick)
        }

        // Save
        view.findViewById<Button>(R.id.editprofile_saveBtn).setOnClickListener {
            if (name.text.toString().isNotBlank() && surnames.text.toString()
                    .isNotBlank() && country.text.toString().isNotBlank() && age.text.toString()
                    .isNotBlank() && website.text.toString().isNotBlank()
            ) {
                user.name = name.text.toString()
                user.surnames = surnames.text.toString()
                user.country = country.text.toString()
                user.category = selectedCategory
                user.age = age.text.toString().toLongOrNull()
                user.website = website.text.toString()
                Log.d("user", user.toString())

                when (selectedCategory) {
                    "User" -> {
                        userDocRef?.set(user.asMap())
                            ?.addOnSuccessListener {
                                showMessageLong("User updated!")
                                findNavController().popBackStack()
                            }?.addOnFailureListener {
                                showMessageShort("Failed!")
                            }
                        artistDocRef?.delete()
                    }
                    "Artist" -> {
                        artistDocRef?.set(user.asMap())
                            ?.addOnSuccessListener {
                                showMessageLong("Artist updated!")
                                findNavController().popBackStack()
                            }?.addOnFailureListener {
                                showMessageShort("Failed!")
                            }
                        userDocRef?.delete()
                    }
                    else -> {
                        Log.d("error", "nosaved")
                    }
                }
            } else {
                Toast.makeText(
                    view.context,
                    "At least one of the fields is empty!",
                    Toast.LENGTH_LONG
                ).show();
            }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}