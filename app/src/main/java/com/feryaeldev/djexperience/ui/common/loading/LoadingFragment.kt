package com.feryaeldev.djexperience.ui.common.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.ui.base.BaseFragment

class LoadingFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

}