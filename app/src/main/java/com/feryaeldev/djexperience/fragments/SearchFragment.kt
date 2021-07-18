package com.feryaeldev.djexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.feryaeldev.djexperience.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Code here


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    private fun search(search: String) {
        CoroutineScope(Dispatchers.IO).launch {

        }
    }

}