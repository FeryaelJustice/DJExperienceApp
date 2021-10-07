package com.feryaeldev.djexperience.ui.view.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.feryaeldev.djexperience.BuildConfig
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.feryaeldev.djexperience.util.authorInstagram
import com.feryaeldev.djexperience.util.authorYoutube
import com.feryaeldev.djexperience.util.checkPermissions
import com.feryaeldev.djexperience.util.djExperienceWebsite
import com.google.firebase.messaging.FirebaseMessaging

class InfoActivity : BaseActivity() {

    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
            resultsMap.forEach {
                Log.d("launcher", "Permission: ${it.key}, granted: ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        findViewById<ImageView>(R.id.info_close).setOnClickListener {
            onBackPressed()
        }
        val infoImage = findViewById<ImageView>(R.id.info_image)
        infoImage.setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data = Uri.parse("https://feryael-justice.jimdosite.com/")
            startActivity(intentURL)
        }
        infoImage.setOnLongClickListener {
            if (checkPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),requestMultiplePermissionLauncher,applicationContext)) {
                //saveImageToInternalStorage(applicationContext, R.drawable.launcher_icon)
                Log.d("save", "trying to save pic")
            } else {
                showMessageLong(getString(R.string.noPermissionsError))
            }
            true
        }
        findViewById<ImageView>(R.id.info_yt).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data = Uri.parse(authorYoutube)
            startActivity(intentURL)
        }
        findViewById<ImageView>(R.id.info_instagram).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data = Uri.parse(authorInstagram)
            startActivity(intentURL)
        }
        findViewById<RelativeLayout>(R.id.info_bottomInfo).setOnClickListener {
            val intentURL = Intent(Intent.ACTION_VIEW)
            intentURL.data =
                Uri.parse(djExperienceWebsite)
            startActivity(intentURL)
        }
        val versionAndCopyright = findViewById<TextView>(R.id.info_bottomInfo_version)
        versionAndCopyright.text =
            getString(R.string.version, BuildConfig.VERSION_NAME)
        versionAndCopyright.setOnClickListener {
            startActivity(Intent(applicationContext, CopyrightActivity::class.java))
        }
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

    override fun onDestroy() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("DJExperience")
        super.onDestroy()
    }

}