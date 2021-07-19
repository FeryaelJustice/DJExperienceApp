package com.feryaeldev.djexperience.base

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.feryaeldev.djexperience.R

abstract class BaseFragment : Fragment() {
    protected fun showMessageShort(message: String) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showMessageLong(message: String) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
    }

    // Fragments

    public fun replaceFragment(fragment: Fragment) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

}