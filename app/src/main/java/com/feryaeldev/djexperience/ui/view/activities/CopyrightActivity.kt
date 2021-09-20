package com.feryaeldev.djexperience.ui.view.activities

import android.os.Bundle
import android.widget.ImageView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity

class CopyrightActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_copyright)

        findViewById<ImageView>(R.id.copyright_close).setOnClickListener {
            onBackPressed()
        }
    }
}