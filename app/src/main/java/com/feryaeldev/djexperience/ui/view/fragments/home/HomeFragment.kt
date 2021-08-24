package com.feryaeldev.djexperience.ui.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.New
import com.feryaeldev.djexperience.data.provider.services.ServiceType
import com.feryaeldev.djexperience.data.provider.services.getRetrofit
import com.feryaeldev.djexperience.data.provider.services.newsapi.NewsApi
import com.feryaeldev.djexperience.data.provider.services.newsapi.NewsApiService
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.ui.common.NewsRecyclerViewAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: NewsRecyclerViewAdapter
    private lateinit var progressCircle: FragmentContainerView

    private val newsList: MutableList<New> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // View
        progressCircle = view.findViewById(R.id.home_fragmentProgressBar)
        mRecyclerView = view.findViewById(R.id.news_rv)

        // initRecyclerView
        mAdapter = NewsRecyclerViewAdapter(newsList)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        mRecyclerView.adapter = mAdapter

        // Http
        progressCircle.visibility = View.VISIBLE
        mRecyclerView.visibility = View.GONE
        getAllNews()

        return view
    }

    private fun getAllNews() {
        CoroutineScope(Dispatchers.IO).launch {
            //delay(2000)
            try {
                val service = getRetrofit(ServiceType.NEWSAPI).create(NewsApiService::class.java)
                val call = service.get("bitcoin", NewsApi.API_KEY)
                    if (call.isSuccessful) {
                        val news: List<New> = call.body()?.articles ?: arrayListOf()
                        newsList.clear()
                        newsList.addAll(news)
                        mAdapter.notifyDataSetChanged()
                    } else {
                        activity?.runOnUiThread {
                            showMessageShort("Error on http call not successful.")
                        }
                    }
                    //progressCircle.visibility = View.GONE
                    //mRecyclerView.visibility = View.VISIBLE
            } catch (e: Error) {
                activity?.runOnUiThread {
                    showMessageShort("Error on http call:${e.message}")
                }
            }
        }
    }
}