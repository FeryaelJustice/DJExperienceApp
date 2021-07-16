package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseActivity
import com.feryaeldev.djexperience.onboarding.OnboardingActivity
import com.feryaeldev.djexperience.settings.Settings
import com.google.android.material.bottomnavigation.BottomNavigationView

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
        //NavigationUI.setupWithNavController(bottomNavigationView, navController)
        /*
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
        */

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            showMessageShort("You pressed Settings!")
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)

        /*
        // Define the listener
        val expandListener = object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Do something when action item collapses
                return true // Return true to collapse action view
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Do something when expanded
                return true // Return true to expand action view
            }
        }

        // Get the MenuItem for the action item
        val actionMenuItem = menu?.findItem(R.id.action_search)

        // Assign the listener to that action item
        actionMenuItem?.setOnActionExpandListener(expandListener)
        */

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

        return super.onCreateOptionsMenu(menu)
    }
}