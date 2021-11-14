package com.feryaeldev.djexperience.data.provider.settings

import android.content.Context
import android.content.SharedPreferences

const val SETTINGS_NAME = "Settings"

class Settings(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(SETTINGS_NAME, Context.MODE_PRIVATE)

    /*
    fun clearSharedPrefs(){
        with(preferences.edit()){
            clear()
            commit()
        }
    }
     */

    fun isFirstOpen(): Boolean = preferences.getBoolean("firstOpen", true)
    fun setFirstOpen(firstOpen: Boolean) {
        with(preferences.edit()) {
            putBoolean("firstOpen", firstOpen)
            commit()
        }
    }

    // Notification Firebase Service token
    fun getFirebaseToken(): String? = preferences.getString("firebaseNotificationToken", "")
    fun setFirebaseToken(token: String) {
        with(preferences.edit()) {
            putString("firebaseNotificationToken", token)
            commit()
        }
    }

    // Remote Config Firebase
    // fun getAuthorMessage(): String? = preferences.getString("authorMessage", "")
    fun setAuthorMessage(authorMessage: String) {
        with(preferences.edit()) {
            putString("authorMessage", authorMessage)
            commit()
        }
    }

    // Account
    fun getUserEmail(): String? = preferences.getString("userEmail", "")
    fun setUserEmail(userEmail: String) {
        with(preferences.edit()) {
            putString("userEmail", userEmail)
            commit()
        }
    }

    fun getUserPassword(): String? = preferences.getString("userPassword", "")
    fun setUserPassword(userPassword: String) {
        with(preferences.edit()) {
            putString("userPassword", userPassword)
            commit()
        }
    }

    /*
    fun clearUserInSharedPrefs() {
        setUserEmail("")
        setUserPassword("")
    }
     */
}