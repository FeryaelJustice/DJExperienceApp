package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.google.firebase.auth.FirebaseAuth

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
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        emailEditText.text.toString(),
                        passwordEditText.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
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