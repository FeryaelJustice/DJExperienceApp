package com.feryaeldev.djexperience.ui.view.activities

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<ImageView>(R.id.settings_close).setOnClickListener {
            onBackPressed()
        }
        findViewById<AppCompatButton>(R.id.settings_clearCache).setOnClickListener {
            try {
                applicationContext.cacheDir.deleteRecursively()
                showMessageLong("Cache deleted!")
            } catch (e: Error) {
                showMessageLong("Cache removal failed: $e")
            }
        }
        findViewById<AppCompatButton>(R.id.settings_screenMode).setOnClickListener {
            try {
                if (isDarkModeOn()) {
                    setScreenMode(applicationContext, false)
                } else {
                    setScreenMode(applicationContext, true)
                }
                // Reload activity
                finish()
                startActivity(intent)
                showMessageShort("Screen mode changed.")
            } catch (e: Error) {
                showMessageLong("Failed to change screen mode: $e")
            }
        }
    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setScreenMode(target: Context, state: Boolean) {
        // State is true when want to put Dark mode.

        val uiManager: UiModeManager =
            target.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        if (Build.VERSION.SDK_INT <= 22) {
            uiManager.enableCarMode(0);
        }

        if (state) {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
        }
    }
}