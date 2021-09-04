package com.feryaeldev.djexperience.data.provider.settings

import android.content.Context
import android.content.SharedPreferences

const val SETTINGS_NAME = "Settings"

class Settings(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    fun isFirstOpen(): Boolean = preferences.getBoolean("firstOpen", true)
    fun setFirstOpen(firstOpen: Boolean) {
        with(preferences.edit()) {
            putBoolean("firstOpen", firstOpen)
            commit()
        }
    }

    // Notificacion Firebase Service token
    fun getFirebaseToken(): String? = preferences.getString("firebaseNotificationToken","")
    fun setFirebaseToken(token: String) {
        with(preferences.edit()) {
            putString("firebaseNotificationToken", token)
            commit()
        }
    }
}