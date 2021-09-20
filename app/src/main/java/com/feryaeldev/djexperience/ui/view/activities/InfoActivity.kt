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
import com.feryaeldev.djexperience.util.saveImageToInternalStorage
import com.google.firebase.messaging.FirebaseMessaging

class InfoActivity : BaseActivity() {

    private var counterPermissions = 0
    private val requestMultiplePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultsMap ->
            resultsMap.forEach {
                Log.d("launcher", "Permission: ${it.key}, granted: ${it.value}")
                if (it.value == true) {
                    counterPermissions++
                }
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
            if(checkPermissions()){
                saveImageToInternalStorage(applicationContext, R.drawable.launcher_icon)
            }else{
                showMessageLong("You don't have permissions.")
            }
            true
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

    private fun checkPermissions(): Boolean {
        requestMultiplePermissionLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        return if (counterPermissions == 2) {
            counterPermissions = 0
            true
        } else {
            counterPermissions = 0
            false
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