package com.feryaeldev.djexperience.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.common.NewsRecyclerViewAdapter
import com.feryaeldev.djexperience.model.domain.New
import kotlinx.coroutines.*

class HomeFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: NewsRecyclerViewAdapter
    private lateinit var progressCircle: FragmentContainerView

    private val newsList: MutableList<New> = arrayListOf()

    @DelicateCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        progressCircle = view.findViewById(R.id.home_fragmentProgressBar)
        progressCircle.visibility = View.VISIBLE

        mRecyclerView = view.findViewById(R.id.news_rv)
        mRecyclerView.visibility = View.GONE
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)

        mAdapter = NewsRecyclerViewAdapter(newsList)

        mRecyclerView.adapter = mAdapter

        GlobalScope.launch(Dispatchers.Main) {
            val res = callServer()
            progressCircle.visibility = View.GONE
            mRecyclerView.visibility = View.VISIBLE
        }

        return view
    }

    private suspend fun callServer() {
        return withContext(Dispatchers.IO) {
            delay(2000)

        }
    }
}