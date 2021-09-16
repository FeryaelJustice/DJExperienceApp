package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.feryaeldev.djexperience.data.provider.settings.Settings
import com.feryaeldev.djexperience.ui.view.activities.onboarding.OnBoardingActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // SETTINGS

        // OnBoarding and other settings
        val settings = Settings(applicationContext)
        if (settings.isFirstOpen()) {
            navigateToOnboarding()
        }

        if (Firebase.auth.currentUser != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<MaterialButton>(R.id.login_btn).setOnClickListener {
            val emailEditText = findViewById<TextInputEditText>(R.id.login_email)
            val passwordEditText = findViewById<TextInputEditText>(R.id.login_password)

            if (!emailEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()) {
                Firebase.auth.signInWithEmailAndPassword(
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
                        showMessageLong(getString(R.string.signInError))
                    }
                }
            } else {
                showMessageLong(getString(R.string.signInError))
            }
        }
        findViewById<TextView>(R.id.signup_txt).setOnClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
            overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)
        }
        findViewById<TextView>(R.id.forgot_password).setOnClickListener {
            startActivity(Intent(applicationContext, RecoverAccountActivity::class.java))
            overridePendingTransition(R.anim.slide_down_reverse, R.anim.slide_up_reverse)
        }
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(applicationContext, OnBoardingActivity::class.java))
        finish()
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