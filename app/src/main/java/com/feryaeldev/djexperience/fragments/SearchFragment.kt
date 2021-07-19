package com.feryaeldev.djexperience.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.base.BaseFragment
import com.feryaeldev.djexperience.data.models.Artist
import com.feryaeldev.djexperience.fragments.search.SearchRecyclerViewAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : BaseFragment() {

    companion object {
        fun newInstance(): SearchFragment = SearchFragment()
    }

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: SearchRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        // Code here
        mRecyclerView = view.findViewById(R.id.fragment_search_recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)
        val db = Firebase.firestore
        val artistsList: MutableList<Artist> = arrayListOf()
        db.collection("artists").get().addOnSuccessListener {
            for (document in it.documents) {
                artistsList.add(
                    artistsList.size,
                    Artist(
                        document["id"].toString(),
                        document["name"].toString(),
                        document["surnames"].toString(),
                        document["nickname"].toString(),
                        document["email"].toString(),
                        document["country"].toString(),
                        document["category"].toString(),
                        document["age"].toString().toInt()
                    )
                )
            }
        }
        mAdapter = SearchRecyclerViewAdapter(artistsList)
        mRecyclerView.adapter = mAdapter

        val search: SearchView = view.findViewById<SearchView>(R.id.fragment_search_searchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showMessageLong("Has buscado:${query}")
                search("")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                showMessageShort("Estás buscando:${newText}")
                search("")
                return false
            }
        })

        // Inflate the layout for this fragment
        return view
    }

    private fun search(search: String) {
        /*CoroutineScope(Dispatchers.IO).launch {

        }*/
    }

}