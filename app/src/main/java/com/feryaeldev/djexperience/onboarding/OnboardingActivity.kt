package com.feryaeldev.djexperience.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.activities.MainActivity
import com.feryaeldev.djexperience.base.BaseActivity
import com.google.android.material.button.MaterialButton

class OnboardingActivity : BaseActivity() {

    private lateinit var onboardingItemsAdapter: OnboardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun navigateToApp() {
        val preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE) ?: return
        if (preferences.getBoolean("firstOpen", true)) {
            with(preferences.edit()) {
                putBoolean("firstOpen", false)
                commit()
            }
        }
        showMessageLong("Welcome to DJ Experience!")
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("goToOnboarding", false)
        startActivity(intent)
        finish()
    }

    private fun setOnboardingItems() {
        onboardingItemsAdapter = OnboardingItemsAdapter(
            listOf(
                OnboardingItem(
                    R.drawable.ic_right,
                    "Primera",
                    "Primera descripción"
                ),
                OnboardingItem(
                    R.drawable.ic_right,
                    "Segunda",
                    "Segunda descripción"
                ),
                OnboardingItem(
                    R.drawable.ic_right,
                    "Tercera",
                    "Tercera descripción"
                ),
            )
        )
        val onboardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onboardingViewPager.adapter = onboardingItemsAdapter
        onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onboardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        // Click listeners
        findViewById<ImageView>(R.id.imageNext).setOnClickListener {
            if (onboardingViewPager.currentItem + 1 < onboardingItemsAdapter.itemCount) {
                onboardingViewPager.currentItem += 1
            } else {
                navigateToApp()
            }
        }
        findViewById<ImageView>(R.id.imagePrevious).setOnClickListener {
            if (onboardingViewPager.currentItem > 0) {
                onboardingViewPager.currentItem -= 1
            }
        }
        findViewById<MaterialButton>(R.id.btnOnboardingStart).setOnClickListener {
            navigateToApp()
        }
    }

    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onboardingItemsAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.let {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
                it.layoutParams = layoutParams
                indicatorsContainer.addView(it)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active_background
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive_background
                    )
                )
            }
        }
    }
}