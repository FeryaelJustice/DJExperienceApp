package com.feryaeldev.djexperience.fragments.artistdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment

class ArtistDetailsFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist_details, container, false)
        val arguments = arguments
        view.findViewById<FrameLayout>(R.id.dr)
        view.findViewById<TextView>(R.id.fragment_artist_details_text).text =
            arguments?.getString("id")
        return view
    }

}