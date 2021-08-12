package com.feryaeldev.djexperience.fragments.search

import android.os.Bundle
import android.util.Log
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

    private var artistsList: MutableList<Artist> = arrayListOf()
    private var initRecyclerView = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Code here

        mRecyclerView = view.findViewById(R.id.fragment_search_recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)

        search("", false) {
            val mAdapter = ArtistsRecyclerViewAdapter(artistsList)
            mRecyclerView.adapter = mAdapter
            Log.d("search", "searched")
        }

        val search: SearchView = view.findViewById(R.id.fragment_search_searchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    // Search and show rv if not blank and not rv init
                    search(query, true) {
                        Log.d("search", "searched")
                        if (!initRecyclerView) {
                            showRecyclerView(view, true)
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    // Show rv if not blank and not rv init
                    if (!initRecyclerView) {
                        showRecyclerView(view, true)
                    }
                } else {
                    // Reset
                    search(newText, false) {
                        Log.d("search", "searched")
                        showRecyclerView(view, false)
                    }
                }
                return false
            }
        })
        return view
    }

    private fun showRecyclerView(view: View, show: Boolean) {
        if (show) {
            initRecyclerView = true
            mRecyclerView.let {
                mRecyclerView.visibility = View.VISIBLE
            }
            view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                View.GONE
        } else {
            initRecyclerView = false
            mRecyclerView.let {
                mRecyclerView.visibility = View.GONE
            }
            view.findViewById<TextView>(R.id.fragment_search_textInfo).visibility =
                View.VISIBLE
        }
    }

    private fun search(search: String?, filter: Boolean, completion: () -> Unit) {
        try {
            artistsList.clear()
            val db = Firebase.firestore
            if (filter) {
                db.collection("artists").whereEqualTo("nickname", search).get()
                    .addOnSuccessListener {
                        if (it.documents.size == 1) {
                            artistsList.add(
                                Artist(
                                    it.documents[0]["id"].toString(),
                                    it.documents[0]["name"].toString(),
                                    it.documents[0]["surnames"].toString(),
                                    it.documents[0]["nickname"].toString(),
                                    it.documents[0]["email"].toString(),
                                    it.documents[0]["country"].toString(),
                                    it.documents[0]["category"].toString(),
                                    it.documents[0]["age"].toString().toInt(),
                                    it.documents[0]["website"].toString()
                                )
                            )
                            mRecyclerView.adapter?.notifyItemChanged(0)
                        } else {
                            showMessageShort("More than one or empty result in DB.")
                        }
                        completion()
                    }.addOnFailureListener {
                        Log.d("error", "Query failed")
                        completion()
                    }
            } else {
                db.collection("artists").get().addOnSuccessListener {
                    it.documents.forEachIndexed { index, document ->
                        artistsList.add(
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
                        mRecyclerView.adapter?.notifyItemChanged(index)
                    }
                    completion()
                }.addOnFailureListener {
                    Log.d("error", "Query failed")
                    completion()
                }
            }
        } catch (e: Error) {
            completion()
            Log.d("error", e.toString())
        }
    }

}