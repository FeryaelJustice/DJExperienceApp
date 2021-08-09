package com.feryaeldev.djexperience.fragments.profile.edit

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
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class EditProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_profile,container,false)

        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid
        val docRef = userId?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                Log.d("datasuccess", "DocumentSnapshot data: ${document.data}")
                val nickname: TextView = view.findViewById(R.id.editprofile_nickname)
                val category: TextView = view.findViewById(R.id.editprofile_category)
                val country: TextView = view.findViewById(R.id.editprofile_country)
                nickname.text = document.data?.get("nickname").toString()
                category.text = document.data?.get("category").toString()
                country.text = document.data?.get("country").toString()
            } else {
                Log.d("nodata", "No such document")
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "get failed with ", exception)
        }
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${userId}.jpg")

        val image: ImageView = view.findViewById(R.id.editprofile_photo)
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        view.findViewById<Button>(R.id.editprofile_saveBtn).setOnClickListener {
            //onBackPressed()
            findNavController().popBackStack()
        }

        view.findViewById<ImageView>(R.id.fragment_editprofile_close).setOnClickListener {
            findNavController().popBackStack()
        }

        //  overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)

        return view
    }
}