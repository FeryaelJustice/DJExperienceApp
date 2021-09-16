package com.feryaeldev.djexperience.ui.view.activities

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<ImageView>(R.id.settings_close).setOnClickListener {
            onBackPressed()
        }
        findViewById<AppCompatButton>(R.id.settings_deleteAccount).setOnClickListener {
            deleteAccount()
        }
        findViewById<AppCompatButton>(R.id.settings_clearCache).setOnClickListener {
            try {
                deleteCache()
                showMessageLong("Cache deleted!")
            } catch (e: Error) {
                showMessageLong("Cache removal failed: $e")
            }
        }
    }

    private fun deleteCache() {
        applicationContext.cacheDir.deleteRecursively()
    }

    private fun deleteAccount() {
        val user = Firebase.auth.currentUser!!
        user.delete().addOnCompleteListener { deleteTask ->
            if (deleteTask.isSuccessful) {
                deleteUserInDB()
            }
        }
    }

    override fun signOut() {
        super.signOut()
        Toast.makeText(applicationContext, "Account successfully deleted.", Toast.LENGTH_SHORT)
            .show()
        Firebase.auth.signOut()
        startActivity(Intent(applicationContext, LoginActivity::class.java))
        finish()
    }

    private fun deleteUserInDB() {
        val db = Firebase.firestore
        val userOrArtistID = Firebase.auth.currentUser?.uid
        val userDocRef = userOrArtistID?.let { db.collection("users").document(it) }
        val artistDocRef = userOrArtistID?.let { db.collection("artists").document(it) }

        userDocRef?.delete()?.addOnCompleteListener { deleteUserTask ->
            if (deleteUserTask.isSuccessful) {
                deleteProfilePicture()
                signOut()
            } else {
                artistDocRef?.delete()?.addOnCompleteListener { deleteArtistTask ->
                    if (deleteArtistTask.isSuccessful) {
                        deleteProfilePicture()
                        signOut()
                    } else {
                        Log.d("error", "Error on delete account")
                    }
                }?.addOnFailureListener {
                    Log.d("error", "Error on delete account")
                }
            }
        }?.addOnFailureListener {
            artistDocRef?.delete()?.addOnCompleteListener { deleteArtistTask ->
                if (deleteArtistTask.isSuccessful) {
                    deleteProfilePicture()
                    signOut()
                } else {
                    Log.d("error", "Error on delete account")
                }
            }?.addOnFailureListener {
                Log.d("error", "Error on delete account")
            }
        }
    }

    private fun deleteProfilePicture() {
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${Firebase.auth.currentUser?.uid}.jpg")
        profilePicRef.delete().addOnCompleteListener {
            Log.d("deletePicture", "success")
        }
    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setScreenMode(target: Context, state: Boolean) {
        // State is true when want to put Dark mode.

        val uiManager: UiModeManager =
            target.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        if (Build.VERSION.SDK_INT <= 22) {
            uiManager.enableCarMode(0);
        }

        if (state) {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
        }
    }

    private fun reloadApp() {
        finish()
        startActivity(intent)
    }
}