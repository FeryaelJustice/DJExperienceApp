package com.feryaeldev.djexperience.ui.view.fragments.create.artist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.TextKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.data.model.enums.Category
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.util.countryIsValid
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CreateArtistFragment : BaseFragment() {

    private var autoCompleteTextViewInputType = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_artist, container, false)

        var uri = Uri.parse("")

        val image: ImageView = view.findViewById(R.id.createArtist_photo)
        val email: TextInputEditText = view.findViewById(R.id.createArtist_email)
        val username: TextInputEditText = view.findViewById(R.id.createArtist_username)
        val name: TextInputEditText = view.findViewById(R.id.createArtist_name)
        val surnames: TextInputEditText = view.findViewById(R.id.createArtist_surnames)
        val country: AutoCompleteTextView = view.findViewById(R.id.createArtist_country)
        val age: TextInputEditText = view.findViewById(R.id.createArtist_age)
        val website: TextInputEditText = view.findViewById(R.id.createArtist_website)

        autoCompleteTextViewInputType = country.inputType // Save input type

        // Get countries
        val countryList = arrayListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val locales = Locale.getAvailableLocales()
            locales.forEach {
                countryList.add(it.displayName)
            }
            countryList.sort()
            activity?.runOnUiThread {
                val autoCompleteTextViewCountryAdapter =
                    ArrayAdapter(
                        view.context,
                        android.R.layout.simple_dropdown_item_1line,
                        countryList
                    )
                autoCompleteTextViewCountryAdapter.setNotifyOnChange(true)
                country.setAdapter(autoCompleteTextViewCountryAdapter)
            }
        }

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
        val storageRef = Firebase.storage.reference

        view.findViewById<MaterialButton>(R.id.createArtist_saveBtn).setOnClickListener {
            if (username.text.toString().isNotBlank() && email.text.toString()
                    .isNotBlank() && name.text.toString()
                    .isNotBlank() && surnames.text.toString()
                    .isNotBlank() && country.text.toString().isNotBlank() && age.text.toString()
                    .isNotBlank() && website.text.toString().isNotBlank()
            ) {
                if (countryIsValid(country.text.toString())) {
                    val user = User()
                    user.id = randomString(28)
                    user.email = email.text.toString()
                    user.username = username.text.toString()
                    user.name = name.text.toString()
                    user.surnames = surnames.text.toString()
                    user.country = country.text.toString()
                    user.category = Category.Artist.name
                    user.age = age.text.toString().toLongOrNull()
                    user.website = website.text.toString()

                    storageRef.child("profile_images/${user.id}.jpg").putFile(uri)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                artistsDocRef.document(user.id!!).set(user)
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
                    showMessageLong("Invalid country! Select it from the list!")
                }
            } else {
                showMessageLong("At least one of the fields is empty!")
            }
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_createArtist_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }

    private fun randomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }


    private fun disableAutoCompleteTextTextView(autoCompleteTextView: AppCompatAutoCompleteTextView) {
        // Disable editable and keyboard
        autoCompleteTextView.inputType = 0
        autoCompleteTextView.keyListener = null
    }

    private fun enableAutoCompleteTextTextView(autoCompleteTextView: AppCompatAutoCompleteTextView) {
        // Enable editable and keyboard
        autoCompleteTextView.inputType = autoCompleteTextViewInputType // saved input type
        autoCompleteTextView.keyListener = TextKeyListener.getInstance()
    }
}