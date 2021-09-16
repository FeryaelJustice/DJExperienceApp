package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.random.Random

class RegisterActivity : BaseActivity() {

    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        findViewById<MaterialButton>(R.id.signup_btn).setOnClickListener {
            val usernameEditText = findViewById<TextInputEditText>(R.id.signup_username)
            val emailEditText = findViewById<TextInputEditText>(R.id.signup_email)
            val passwordEditText = findViewById<TextInputEditText>(R.id.signup_password)
            val repeatPasswordEditText = findViewById<TextInputEditText>(R.id.signup_repeatpassword)

            if (!usernameEditText.text.isNullOrBlank() && !emailEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank() && !repeatPasswordEditText.text.isNullOrBlank()) {
                if (passwordEditText.text.toString() == repeatPasswordEditText.text.toString()) {
                    Firebase.auth.createUserWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val db = Firebase.firestore
                            val user = hashMapOf(
                                "id" to (Firebase.auth.currentUser?.uid ?: randomString(28)),
                                "email" to emailEditText.text.toString(),
                                "username" to usernameEditText.text.toString(),
                                "name" to "Default name",
                                "surnames" to "Default surnames",
                                "age" to 0,
                                "country" to "Earth",
                                "category" to "User",
                                "following" to arrayListOf<String>(),
                                "website" to "www.google.com"
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
                    }.addOnFailureListener {
                        showMessageLong(getString(R.string.signUpError))
                    }
                } else {
                    showMessageLong(getString(R.string.passwordsNotMatch))
                }
            } else {
                showMessageLong(getString(R.string.someEmptyFieldsError))
            }
        }
    }

    private fun randomString(stringLength: Int): String {
        return (1..stringLength)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
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

    override fun onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("DJExperience")
        super.onDestroy()
    }
}