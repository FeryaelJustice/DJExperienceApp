package com.feryaeldev.djexperience.ui.view.activities

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
            try{
                applicationContext.cacheDir.deleteRecursively()
                showMessageLong("Cache deleted!")
            }catch (e: Error){
                showMessageLong("Cache removal failed: $e")
            }
        }
    }
}