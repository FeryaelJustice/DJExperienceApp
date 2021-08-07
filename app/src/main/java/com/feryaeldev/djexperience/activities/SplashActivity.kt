package com.feryaeldev.djexperience.activities

import android.content.Intent
import android.os.Bundle
import com.feryaeldev.djexperience.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }
}