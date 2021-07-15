package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.feryaeldev.djexperience.data.enums.ArtistType
import com.feryaeldev.djexperience.data.models.Artist
import com.feryaeldev.djexperience.data.models.User
import com.feryaeldev.djexperience.data.viewmodels.ArtistViewModel
import com.feryaeldev.djexperience.data.viewmodels.UserViewModel
import com.feryaeldev.djexperience.fragments.HomeFragment
import com.feryaeldev.djexperience.fragments.ProfileFragment
import com.feryaeldev.djexperience.fragments.SearchFragment
import com.feryaeldev.djexperience.onboarding.OnboardingActivity
import com.feryaeldev.djexperience.settings.Settings
import com.google.android.gms.ads.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : BaseActivity() {

    lateinit var mAdView: AdView
    private val userViewModel: UserViewModel by viewModels()
    private val artistViewModel: ArtistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // VIEW MODELS
        userViewModel.getUser().observe(this, {
            findViewById<TextView>(R.id.tv1).text = it.edad.toString()
        })
        artistViewModel.getArtist().observe(this, {
            findViewById<TextView>(R.id.tv2).text = it.edad.toString()
        })

        // SETTINGS
        //val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE) ?: return
        val settings = Settings(applicationContext)
        if (settings.isFirstOpen()) {
            navigateToOnboarding()
        }

        // CRASHLYTICS
        val crashButton = Button(this)
        crashButton.text = "Crash!"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }
        findViewById<Button>(R.id.id).setOnClickListener {
            userViewModel.setUser(
                User(
                    "Fer",
                    userViewModel.getUser().value!!.edad + 1
                )
            )
        }
        findViewById<Button>(R.id.id2).setOnClickListener {
            artistViewModel.setArtist(
                Artist(
                    "Fer", ArtistType.DJ,
                    artistViewModel.getArtist().value!!.edad + 1
                )
            )
        }

        addContentView(
            crashButton,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ),
        )

        // ADS
        MobileAds.initialize(this) {}

        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

        // Fragments
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.selectedItemId = R.id.action_home
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    replaceFragment(HomeFragment.newInstance())
                    true
                }
                R.id.action_search -> {
                    replaceFragment(SearchFragment.newInstance())
                    true
                }
                R.id.action_profile -> {
                    replaceFragment(ProfileFragment.newInstance())
                    true
                }
                else -> false
            }
        }

    }

    private fun navigateToOnboarding() {
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
        finish()
    }
}