package com.feryaeldev.djexperience.ui.view.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

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
                        showMessageLong("This mail doesn't belong to any account.")
                    }
                }
            } else {
                showMessageLong("You must enter a valid email.")
            }
        }
    }

    override fun onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("DJExperience")
        super.onDestroy()
    }
}