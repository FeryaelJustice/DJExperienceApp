package com.feryaeldev.djexperience.base

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.feryaeldev.djexperience.R
import com.google.firebase.FirebaseApp

abstract class BaseActivity : AppCompatActivity() {

    protected fun showMessageShort(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showMessageLong(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }
}