package com.feryaeldev.djexperience.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.activities.EditProfileActivity
import com.feryaeldev.djexperience.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class ProfileFragment : BaseFragment() {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid
        val docRef = userId?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                Log.d("datasuccess", "DocumentSnapshot data: ${document.data}")
                val nickname: TextView = view.findViewById(R.id.profile_nickname)
                val category: TextView = view.findViewById(R.id.profile_category)
                val country: TextView = view.findViewById(R.id.profile_country)
                nickname.text = document.data?.get("nickname").toString()
                category.text = document.data?.get("category").toString()
                country.text = document.data?.get("country").toString()
            } else {
                Log.d("nodata", "No such document")
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "get failed with ", exception)
        }
        val profileRef =
            Firebase.storage.reference.child("users/${userId}/profilepicture.png")
        profileRef.downloadUrl.addOnSuccessListener {
            val image: ImageView = view.findViewById(R.id.profile_photo)
            Picasso.get().load(it).placeholder(R.drawable.ic_baseline_person_24).error(R.drawable.ic_baseline_person_24).into(image)
        }
        view.findViewById<Button>(R.id.profile_editProfileBtn).setOnClickListener {
            startActivity(Intent(view.context, EditProfileActivity::class.java))
        }

        return view
    }
}