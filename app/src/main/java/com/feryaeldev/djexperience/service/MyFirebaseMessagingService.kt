package com.feryaeldev.djexperience.service

import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private var lastMessage = ""
    private var token = ""

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        lastMessage = remoteMessage.notification?.title.toString()
        Toast.makeText(applicationContext,remoteMessage.notification?.title,Toast.LENGTH_LONG).show()
    }
}