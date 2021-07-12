package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.feryaeldev.djexperience.onboarding.OnboardingActivity
import com.feryaeldev.djexperience.settings.Settings

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE) ?: return
        val settings = Settings(applicationContext)
        if (settings.isFirstOpen()) {
            navigateToOnboarding()
        }
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
        finish()
    }
}