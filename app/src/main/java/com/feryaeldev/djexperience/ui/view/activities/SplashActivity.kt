package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.os.Bundle
import com.feryaeldev.djexperience.ui.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
}