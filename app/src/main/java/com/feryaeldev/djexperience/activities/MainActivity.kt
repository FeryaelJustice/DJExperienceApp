package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.feryaeldev.djexperience.onboarding.OnboardingActivity

class MainActivity : BaseActivity() {

    private var goToOnBoardingActivity: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToOnBoardingActivity = intent.getBooleanExtra("goToOnboarding", true)

        if (goToOnBoardingActivity) {
            navigateToOnboarding()
        }
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
        finish()
    }
}