package com.feryaeldev.djexperience.settings

import android.content.Context

val SETTINGS_NAME = "Settings"
val SETTINGS_LANGUAGE = "Language"

class Settings(context: Context) {

    val preferences = context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    fun isFirstOpen(): Boolean = preferences.getBoolean("firstOpen", true)
    fun setFirstOpen(firstOpen: Boolean) {
        with(preferences.edit()) {
            putBoolean("firstOpen", firstOpen)
            commit()
        }
    }

    fun getLanguage(): String? = preferences.getString("language", "")
    fun setLanguage(language: String) {
        with(preferences.edit()) {
            putString("language", language)
            commit()
        }
    }
}