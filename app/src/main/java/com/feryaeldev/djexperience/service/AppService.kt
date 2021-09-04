package com.feryaeldev.djexperience.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AppService: Service() {

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("service","AppService bind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("service","AppService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("service","AppService init")
        val runnable = Runnable {
            for (i in 1..10){
                Log.d("service","Service doing: $i")
                Thread.sleep(1000)
            }
            stopSelf()
        }
        val thread = Thread(runnable)
        thread.start()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("service","AppService destroyed")
    }
}