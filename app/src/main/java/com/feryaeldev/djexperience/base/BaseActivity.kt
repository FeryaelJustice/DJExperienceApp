package com.feryaeldev.djexperience.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.feryaeldev.djexperience.settings.Settings

abstract class BaseActivity : AppCompatActivity() {

    protected fun showMessageShort(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showMessageLong(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}