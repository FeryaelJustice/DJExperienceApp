package com.feryaeldev.djexperience.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.activities.MainActivity
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        findViewById<MaterialButton>(R.id.btnOnboardingStart).setOnClickListener {
            navigateToApp()
        }
    }

    private fun navigateToApp(){
        val intent = Intent(applicationContext,MainActivity::class.java)
        intent.putExtra("goToOnboarding",false)
        startActivity(intent)
        finish()
    }
}