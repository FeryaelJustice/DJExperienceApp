package com.feryaeldev.djexperience.ui.base

import android.widget.Toast
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    protected fun showMessageShort(message: String) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showMessageLong(message: String) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
    }
}