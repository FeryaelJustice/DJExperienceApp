package com.feryaeldev.djexperience.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

class EditProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val db = Firebase.firestore
        val userId = Firebase.auth.currentUser?.uid
        val docRef = userId?.let { db.collection("users").document(it) }
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                Log.d("datasuccess", "DocumentSnapshot data: ${document.data}")
                val nickname: TextView = findViewById(R.id.editprofile_nickname)
                val category: TextView = findViewById(R.id.editprofile_category)
                val country: TextView = findViewById(R.id.editprofile_country)
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
            val image: ImageView = findViewById(R.id.editprofile_photo)
            Picasso.get().load(it).placeholder(R.drawable.ic_baseline_person_24)
                .error(R.drawable.ic_baseline_person_24).into(image)
        }
        findViewById<Button>(R.id.editprofile_saveBtn).setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)
        this.finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)
    }
}