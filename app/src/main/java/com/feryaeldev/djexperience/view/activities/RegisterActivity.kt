package com.feryaeldev.djexperience.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<Button>(R.id.signup_btn).setOnClickListener {
            val emailEditText = findViewById<EditText>(R.id.signup_email)
            val passwordEditText = findViewById<EditText>(R.id.signup_password)
            val repeatPasswordEditText = findViewById<EditText>(R.id.signup_repeatpassword)

            if (!emailEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank() && !repeatPasswordEditText.text.isNullOrBlank()) {
                if (passwordEditText.text.toString() == repeatPasswordEditText.text.toString()) {
                    Firebase.auth.createUserWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val db = Firebase.firestore
                            val user = hashMapOf(
                                "email" to emailEditText.text.toString(),
                                "nickname" to "",
                                "name" to "",
                                "surnames" to "",
                                "age" to 0,
                                "country" to "",
                                "category" to "",
                                "age" to 0,
                                "following" to arrayListOf<String>(),
                                "website" to ""
                            )
                            Firebase.auth.currentUser?.let { fUser ->
                                db.collection("users").document(fUser.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Log.d(
                                            "UserAddedToDB",
                                            "DocumentSnapshot added successfully"
                                        )
                                        showMessageShort("Error on creating user in DB, auth success.")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("UserFailedToAddToDB", "Error adding document", e)
                                    }
                            }
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            overridePendingTransition(
                                R.anim.slide_down_reverse,
                                R.anim.slide_up_reverse
                            )
                            finish()
                        } else {
                            showMessageLong(getString(R.string.signUpError))
                        }
                    }
                } else {
                    showMessageLong(getString(R.string.signUpError))
                }
            } else {
                showMessageLong(getString(R.string.signUpError))
            }
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