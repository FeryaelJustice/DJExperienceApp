package com.feryaeldev.djexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.feryaeldev.djexperience.R

class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val logged = false
        return if (logged) {
            inflater.inflate(R.layout.fragment_profile, container, false)
        } else {
            inflater.inflate(R.layout.fragment_profile_notlogged, container, false)
        }
    }
}