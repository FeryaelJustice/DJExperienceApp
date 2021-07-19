package com.feryaeldev.djexperience.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

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
                val profileRef =
                    Firebase.storage.reference.child("users/${Firebase.auth.currentUser?.uid}/profilepicture.jpg")
                profileRef.downloadUrl.addOnSuccessListener {
                    val image: ImageView = view.findViewById(R.id.profile_photo)
                    Glide.with(view.context).load(it).fitCenter()
                        .dontAnimate()
                        .into(image)
                }
            } else {
                Log.d("nodata", "No such document")
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "get failed with ", exception)
        }
        return view
    }
}