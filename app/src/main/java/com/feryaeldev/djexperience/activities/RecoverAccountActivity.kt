package com.feryaeldev.djexperience.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_account)

        findViewById<TextView>(R.id.recover_btn).setOnClickListener {
            val email = findViewById<EditText>(R.id.recover_email).text.toString()
            if (email.isNotBlank()) {
                Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showMessageLong("Check your email!")
                        onBackPressed()
                    } else {
                        showMessageLong("Failed to send an email.")
                    }
                }
            } else {
                showMessageLong("You must enter a valid email.")
            }
        }
    }
}