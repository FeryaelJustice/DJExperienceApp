package com.feryaeldev.djexperience.fragments.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.common.ArtistsRecyclerViewAdapter
import com.feryaeldev.djexperience.data.models.Artist
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView

    // All the original artists to return to the initial list without querying firebase
    private var artistsListOriginal: MutableList<Artist> = arrayListOf()
    private var artistsListTemp: MutableList<Artist> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        var initRecyclerView = false

        // Code here
        artistsListOriginal.clear()
        artistsListTemp.clear()

        mRecyclerView = view.findViewById(R.id.fragment_search_recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val db = Firebase.firestore
        db.collection("artists").get().addOnSuccessListener {
            for (document in it.documents) {
                artistsListTemp.add(
                    Artist(
                        document["id"].toString(),
                        document["name"].toString(),
                        document["surnames"].toString(),
                        document["nickname"].toString(),
                        document["email"].toString(),
                        document["country"].toString(),
                        document["category"].toString(),
                        document["age"].toString().toInt(),
                        document["website"].toString()
                    )
                )
            }
        }
        //lateinit var mAdapter: ArtistsRecyclerViewAdapter
        artistsListOriginal.addAll(artistsListTemp)
        val mAdapter = ArtistsRecyclerViewAdapter(artistsListTemp)
        mRecyclerView.adapter = mAdapter

        val search: SearchView = view.findViewById(R.id.fragment_search_searchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!initRecyclerView) {
                    initRecyclerView = true
                    mRecyclerView.let {
                        mRecyclerView.visibility = View.VISIBLE
                    }
                    view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                        View.GONE
                } else {
                    search(query)
                    if (query.isNullOrBlank()) {
                        initRecyclerView = false
                        mRecyclerView.let {
                            mRecyclerView.visibility = View.GONE
                        }
                        view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                            View.VISIBLE
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!initRecyclerView) {
                    initRecyclerView = true
                    mRecyclerView.let {
                        mRecyclerView.visibility = View.VISIBLE
                    }
                    view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                        View.GONE
                } else {
                    if (newText.isNullOrBlank()) {
                        initRecyclerView = false
                        mRecyclerView.let {
                            mRecyclerView.visibility = View.GONE
                        }
                        view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                            View.VISIBLE
                    }
                }
                return false
            }
        })
        return view
    }

    private fun search(search: String?) {
        val searchText = search?.lowercase()
        artistsListTemp.clear()
        if (searchText?.length!! > 0) {
            artistsListOriginal.forEach {
                if (it.id?.contains(searchText) == true) {
                    artistsListTemp.add(it)
                }
            }
            //mRecyclerView.adapter?.notifyItemChanged()
            mRecyclerView.adapter?.notifyDataSetChanged()
        } else {
            artistsListTemp.addAll(artistsListOriginal)
            //mRecyclerView.adapter?.notifyItemChanged()
            mRecyclerView.adapter?.notifyDataSetChanged()
        }
    }

}