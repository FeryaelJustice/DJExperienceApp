package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.feryaeldev.djexperience.BuildConfig
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity

class InfoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        findViewById<ImageView>(R.id.info_close).setOnClickListener {
            onBackPressed()
        }
        findViewById<ImageView>(R.id.info_yt).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data = Uri.parse("https://www.youtube.com/channel/UCDYgeJeEqB9tMCE0EqcXE3Q")
            startActivity(intentURL)
        }
        findViewById<ImageView>(R.id.info_instagram).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data = Uri.parse("https://www.instagram.com/feryaeljustice")
            startActivity(intentURL)
        }
        findViewById<RelativeLayout>(R.id.info_bottomInfo).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data =
                Uri.parse("https://feryaeljustice.notion.site/DJ-Experience-d4d4928723af4250a258ac638d7f09a0")
            startActivity(intentURL)
        }
        findViewById<TextView>(R.id.info_bottomInfo_version).text =
            getString(R.string.version, BuildConfig.VERSION_NAME)
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