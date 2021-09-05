package com.feryaeldev.djexperience.ui.view.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
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
import com.feryaeldev.djexperience.util.HttpResponseStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: NewsRecyclerViewAdapter
    private lateinit var progressCircle: FragmentContainerView

    private var newsList: MutableList<New> = arrayListOf()

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

        // Button create artist
        view.findViewById<Button>(R.id.home_createArtist).setOnClickListener {
            findNavController().navigate(
                R.id.action_homeFragment_to_createArtistFragment,
                arguments
            )
        }

        //Firebase.firestore.collection("users").document("01mkPXmpSoa77t7Yh4iPxOupXJ72").delete()

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
                val call = service.get("djs", NewsApi.API_KEY)
                if (call.code() == HttpResponseStatus.SUCCESS.code) {
                    val news = call.body()?.articles ?: arrayListOf()
                    activity?.runOnUiThread {
                        //showMessageLong(news.toString())
                        newsList.clear()
                        news.forEachIndexed { index, element ->
                            /*val translator = TranslateOptions.getDefaultInstance().service
                            val translatedNew = New(
                                element.sourceId,
                                translator.translate(
                                    element.sourceName,
                                    Translate.TranslateOption.targetLanguage("es")
                                ).translatedText,
                                element.author,
                                translator.translate(
                                    element.title,
                                    Translate.TranslateOption.targetLanguage("es")
                                ).translatedText,
                                translator.translate(
                                    element.description,
                                    Translate.TranslateOption.targetLanguage("es")
                                ).translatedText,
                                element.url,
                                element.urlToImage,
                                element.publishedAt,
                                translator.translate(
                                    element.content,
                                    Translate.TranslateOption.targetLanguage("es")
                                ).translatedText
                            )*/
                            //newsList.add(index, translatedNew)
                            newsList.add(index, element)
                            mAdapter.notifyItemChanged(index)
                        }
                        /*
                        newsList.addAll(news)
                        mAdapter.notifyDataSetChanged()
                         */
                        progressCircle.visibility = View.GONE
                        mRecyclerView.visibility = View.VISIBLE
                    }
                } else {
                    activity?.runOnUiThread {
                        showMessageShort("Error on http call (code) not successful: $call")
                    }
                }

            } catch (e: Error) {
                activity?.runOnUiThread {
                    showMessageShort("Error on http call:${e.message}")
                }
            } finally {

            }
        }
    }
}