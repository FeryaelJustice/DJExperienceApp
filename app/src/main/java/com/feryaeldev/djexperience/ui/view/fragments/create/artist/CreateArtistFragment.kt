package com.feryaeldev.djexperience.ui.view.fragments.create.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseFragment

class CreateArtistFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_artist, container, false)

        view.findViewById<Button>(R.id.createArtist_saveBtn).setOnClickListener {
            Toast.makeText(view.context,"Save function very soon...",Toast.LENGTH_SHORT).show();
        }

        // Back
        view.findViewById<ImageView>(R.id.fragment_createArtist_close).setOnClickListener {
            findNavController().popBackStack()
        }

        return view
    }
}