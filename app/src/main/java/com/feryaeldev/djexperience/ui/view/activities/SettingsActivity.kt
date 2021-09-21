package com.feryaeldev.djexperience.ui.view.activities

import android.app.AlertDialog
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.provider.settings.Settings
import com.feryaeldev.djexperience.ui.base.BaseActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SettingsActivity : BaseActivity() {

    private val userOrArtistID = Firebase.auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        findViewById<ImageView>(R.id.settings_close).setOnClickListener {
            onBackPressed()
        }
        findViewById<AppCompatButton>(R.id.settings_deleteAccount).setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.userIsAwareToDeleteAccQuestion))
            builder.setMessage(getString(R.string.userIsAwareToDeleteAccDescription))
            builder.setPositiveButton(getString(R.string.yes)) { dialogInterface, _ ->
                deleteAccount()
                dialogInterface.dismiss()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                showMessageLong(getString(R.string.wisechoice))
                dialog.cancel()
            }
            val alert = builder.create()
            alert.show()
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
        val user = Firebase.auth.currentUser
        user?.delete()?.addOnCompleteListener { deleteTask ->
            if (deleteTask.isSuccessful) {
                // Now user doesnt exist, get id before this to delete it from DB
                try {
                    deleteUserInDB()
                    showMessageShort("Account successfully deleted.")
                    signOut()
                } catch (e: Error) {
                    Log.d("error", "Error deleting account:$e")
                    showMessageShort("Error deleting account: $e")
                    reAuthenticate(user)
                }
            }
        }?.addOnFailureListener {
            Log.d("error", "Error deleting account:$it")
            showMessageShort("Error deleting account: $it")
            reAuthenticate(user)
        }
    }

    private fun reAuthenticate(user: FirebaseUser) {
        showMessageLong("Reautheticating...")
        val settings = Settings(applicationContext)
        val credential = EmailAuthProvider
            .getCredential(settings.getUserEmail().orEmpty(), settings.getUserPassword().orEmpty())
        user.reauthenticate(credential)
            .addOnCompleteListener {
                Log.d("auth", "User re-authenticated.")
                showMessageLong("Reauthenticated!")
            }
        reloadApp(Intent(applicationContext, MainActivity::class.java))
    }

    override fun signOut() {
        super.signOut()
        Firebase.auth.signOut()
        reloadApp(Intent(applicationContext, LoginActivity::class.java))
    }

    private fun deleteUserInDB() {
        val db = Firebase.firestore
        val userDocRef = userOrArtistID?.let { db.collection("users").document(it) }
        val artistDocRef = userOrArtistID?.let { db.collection("artists").document(it) }

        userDocRef?.delete()?.addOnCompleteListener { deleteUserTask ->
            if (deleteUserTask.isSuccessful) {
                deleteProfilePicture(userOrArtistID)
            } else {
                artistDocRef?.delete()?.addOnCompleteListener { deleteArtistTask ->
                    if (deleteArtistTask.isSuccessful) {
                        deleteProfilePicture(userOrArtistID)
                    } else {
                        Log.d("error", "Error on delete account in DB")
                        showMessageLong("Error deleting account in DB. Not successful!")
                    }
                }?.addOnFailureListener {
                    Log.d("error", "Error on delete account in DB")
                    showMessageLong("Error deleting account in DB: $it")
                }
            }
        }?.addOnFailureListener {
            artistDocRef?.delete()?.addOnCompleteListener { deleteArtistTask ->
                if (deleteArtistTask.isSuccessful) {
                    deleteProfilePicture(userOrArtistID)
                } else {
                    Log.d("error", "Error on delete account in DB")
                    showMessageLong("Error deleting account in DB. Not successful!")
                }
            }?.addOnFailureListener {
                Log.d("error", "Error on delete account in DB")
                showMessageLong("Error deleting account in DB: $it")
            }
        }
    }

    private fun deleteProfilePicture(userOrArtistID: String?) {
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/$userOrArtistID.jpg")
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
            uiManager.enableCarMode(0)
        }

        if (state) {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } else {
            uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
        }
    }

    private fun reloadApp(intent: Intent) {
        finish()
        startActivity(intent)
    }
}