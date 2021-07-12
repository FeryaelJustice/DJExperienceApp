package com.feryaeldev.djexperience.settings

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

    fun getSessionToken(): String? = preferences.getString("sessionToken", "")
    fun setSessionToken(token: String) {
        with(preferences.edit()) {
            putString("sessionToken", token)
            commit()
        }
    }
}