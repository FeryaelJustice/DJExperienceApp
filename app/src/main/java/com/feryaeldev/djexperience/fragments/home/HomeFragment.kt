package com.feryaeldev.djexperience.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.common.NewsRecyclerViewAdapter
import com.feryaeldev.djexperience.data.models.New

class HomeFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: NewsRecyclerViewAdapter

    private val newsList: MutableList<New> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        mRecyclerView = view.findViewById(R.id.news_rv)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)

        mAdapter = NewsRecyclerViewAdapter(newsList)

        mRecyclerView.adapter = mAdapter

        return view
    }
}