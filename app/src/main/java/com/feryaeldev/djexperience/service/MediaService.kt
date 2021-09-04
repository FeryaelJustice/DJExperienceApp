package com.feryaeldev.djexperience.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log

class MediaService : Service() {

    lateinit var mediaPlayer: MediaPlayer

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("service", "MediaService bind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("service", "MediaService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service", "MediaService init")

        mediaPlayer = MediaPlayer.create(applicationContext, Settings.System.DEFAULT_RINGTONE_URI)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        Thread.sleep(5000)

        stopSelf()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        Log.d("service", "MediaService destroyed")
    }
}