package com.feryaeldev.djexperience.ui.view.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.feryaeldev.djexperience.data.provider.settings.Settings
import com.feryaeldev.djexperience.service.AppService
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : BaseActivity() {

    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        // Firebase Messaging
        firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Token
                val token = task.result
                val settings = Settings(applicationContext)
                settings.setFirebaseToken(token)
                settings.getFirebaseToken()?.let { finalToken ->
                    Log.d("token", finalToken)
                    //showMessageLong(it1)
                }

                // Topics
                firebaseMessaging.subscribeToTopic("DJExperienceUsers")

                // Metadata
                val url = intent.getStringExtra("appUrl")
                url?.let { finalUrl ->
                    Log.d("url", "Ha llegado la url de la app: $finalUrl")
                }
            }
        }

        // My Services
        startService(Intent(applicationContext, AppService::class.java))
        // startService(Intent(applicationContext, MediaService::class.java))

        // Init App
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        firebaseMessaging.unsubscribeFromTopic("DJExperience")
        super.onDestroy()
    }
}