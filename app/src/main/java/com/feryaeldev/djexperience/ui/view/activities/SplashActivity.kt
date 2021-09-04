package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.os.Bundle
import com.feryaeldev.djexperience.service.AppService
import com.feryaeldev.djexperience.service.MediaService
import com.feryaeldev.djexperience.ui.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        startService(Intent(applicationContext, AppService::class.java))
        // startService(Intent(applicationContext, MediaService::class.java))

        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
}