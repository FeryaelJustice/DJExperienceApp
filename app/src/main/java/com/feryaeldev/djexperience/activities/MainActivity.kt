package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.feryaeldev.djexperience.onboarding.OnboardingActivity
import com.feryaeldev.djexperience.settings.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {

    //lateinit var mAdView: AdView
    //private val userViewModel: UserViewModel by viewModels()
    //private val artistViewModel: ArtistViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // SETTINGS

        // Onboarding and other settings
        //val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE) ?: return
        val settings = Settings(applicationContext)
        if (settings.isFirstOpen()) {
            navigateToOnboarding()
        }
        // Initialize toolbar
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // FRAGMENTS BOTTOM NAV AND NAVIGATION COMPONENT

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener {
            // Avoid reload
        }

        messageFirebaseAnalytics()

        // VIEW MODELS

        /*
        userViewModel.getUser().observe(this, {
            findViewById<TextView>(R.id.tv1).text = it.edad.toString()
        })
        artistViewModel.getArtist().observe(this, {
            findViewById<TextView>(R.id.tv2).text = it.edad.toString()
        })
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
        */

        // ADS

        /*
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
        */

        // CRASHLYTICS

        /*val crashButton = Button(this)
        crashButton.text = "Crash!"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(
            crashButton,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ),
        )*/
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(applicationContext, OnboardingActivity::class.java))
        finish()
    }

    private fun messageFirebaseAnalytics() {
        val analytics = FirebaseAnalytics.getInstance(applicationContext)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("Init", bundle)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_info -> {
            val intentInfo = Intent(applicationContext, InfoActivity::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intentInfo)
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
            } else {
                startActivity(intentInfo)
            }
            true
        }
        R.id.action_settings -> {
            showMessageShort("You pressed Settings!")
            true
        }
        R.id.action_signout -> {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)

        // For menu search, DO THE SEARCH INSIDE THE ACTIVITIES OR FRAGMENTS, NOT LIKE THIS
        /*
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.queryHint = "Search artists"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                // Here code
                return false
            }
        })
        */

        return super.onCreateOptionsMenu(menu)
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