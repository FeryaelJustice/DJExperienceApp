package com.feryaeldev.djexperience.ui.view.activities.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.view.activities.LoginActivity
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.feryaeldev.djexperience.data.model.domain.OnBoardingItem
import com.feryaeldev.djexperience.data.provider.settings.Settings
import com.google.android.material.button.MaterialButton
import java.util.*

class OnBoardingActivity : BaseActivity() {

    private lateinit var onBoardingItemsAdapter: OnBoardingItemsAdapter
    private lateinit var indicatorsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        setOnBoardingItems()
        setupIndicators()
        setCurrentIndicator(0)
    }

    private fun navigateToApp() {
        val settings = Settings(applicationContext)
        if (settings.isFirstOpen()) {
            settings.setFirstOpen(false)
        }
        val language = Locale.getDefault().displayLanguage
        showMessageLong("Welcome to DJ Experience! Language: $language")
        val intent = Intent(applicationContext, LoginActivity::class.java)
        intent.putExtra("goToOnboarding", false)
        startActivity(intent)
        finish()
    }

    private fun setOnBoardingItems() {
        onBoardingItemsAdapter = OnBoardingItemsAdapter(
            listOf(
                OnBoardingItem(
                    R.drawable.launcher_image,
                    "Welcome to DJExperience!",
                    "In this app you will find all you need about DJ's."
                ),
                OnBoardingItem(
                    R.drawable.ic_baseline_person_24,
                    "With account system",
                    "You have to login to use the app, because it syncs all your data (who you follow and other)"
                ),
                OnBoardingItem(
                    R.drawable.ic_baseline_play_arrow_24,
                    "Quick introduction",
                    "You only have to search your favourite artists (lowercase sensitive and no spaces) and create your own DJ's."
                ),
            )
        )
        val onBoardingViewPager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        onBoardingViewPager.adapter = onBoardingItemsAdapter
        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
        (onBoardingViewPager.getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER

        // Click listeners
        findViewById<ImageView>(R.id.imageNext).setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onBoardingItemsAdapter.itemCount) {
                onBoardingViewPager.currentItem += 1
            } else {
                navigateToApp()
            }
        }
        findViewById<ImageView>(R.id.imagePrevious).setOnClickListener {
            if (onBoardingViewPager.currentItem > 0) {
                onBoardingViewPager.currentItem -= 1
            }
        }
        findViewById<MaterialButton>(R.id.btnOnboardingStart).setOnClickListener {
            navigateToApp()
        }
    }

    private fun setupIndicators() {
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        val indicators = arrayOfNulls<ImageView>(onBoardingItemsAdapter.itemCount)
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