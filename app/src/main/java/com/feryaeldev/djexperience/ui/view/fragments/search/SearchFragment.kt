package com.feryaeldev.djexperience.ui.view.fragments.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.ui.common.ArtistsRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchFragment : BaseFragment() {

    private lateinit var mainView: View
    private lateinit var search: SearchView
    private lateinit var mRecyclerView: RecyclerView
    private var artistsList: MutableList<User> = arrayListOf()
    private var initRecyclerView = false
    private lateinit var progressCircle: FragmentContainerView
    private var categoryFilterText = "Artist"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        mainView = view

        // Code here
        progressCircle = view.findViewById(R.id.search_fragmentProgressBar)

        mRecyclerView = view.findViewById(R.id.fragment_search_recyclerView)
        mRecyclerView.layoutManager = LinearLayoutManager(view.context)

        val categoryFilter: Spinner = view.findViewById(R.id.fragment_search_spinnerSearch)
        val userListTypes = resources.getStringArray(R.array.user_categories)
        val adapter =
            ArrayAdapter(view.context, android.R.layout.simple_spinner_item, userListTypes)
        categoryFilter.adapter = adapter
        categoryFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                categoryFilterText = userListTypes[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d("todo", p0.toString())
            }
        }

        search("", false, categoryFilterText) {
            val mAdapter = ArtistsRecyclerViewAdapter(artistsList)
            mRecyclerView.adapter = mAdapter
            Log.d("search", "searched")
        }

        search = view.findViewById(R.id.fragment_search_searchView)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    progressCircle.visibility = View.VISIBLE
                    // Search and show rv if not blank and not rv init
                    search(query, true, categoryFilterText) {
                        Log.d("search", "searched")
                        if (!initRecyclerView) {
                            showRecyclerView(view, true)
                        }
                        progressCircle.visibility = View.GONE
                    }
                } else {
                    showMessageShort("Empty search")
                    resetSearch()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                /*
                if (!newText.isNullOrBlank()) {
                    progressCircle.visibility = View.VISIBLE
                    // Show rv if not blank and not rv init
                    if (!initRecyclerView) {
                        showRecyclerView(view, true)
                    }
                    progressCircle.visibility = View.GONE
                }*/
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

    private fun search(
        search: String?,
        filter: Boolean,
        filterCategory: String,
        completion: () -> Unit
    ) {
        try {
            artistsList.clear()
            val db = Firebase.firestore
            if (filter) {
                if (filterCategory == "Artist") {
                    db.collection("artists").whereEqualTo("username", search).get()
                        .addOnSuccessListener {
                            if (it.documents.size == 1) {
                                if (it.documents[0]["id"].toString() != Firebase.auth.currentUser?.uid) {
                                    artistsList.add(
                                        User(it.documents[0]["id"].toString())
                                    )
                                    mRecyclerView.adapter?.notifyItemChanged(0)
                                }
                            } else {
                                showMessageShort("More than one or empty result in DB.")
                            }
                            completion()
                        }.addOnFailureListener {
                            Log.d("error", "Query failed")
                            completion()
                        }
                } else {
                    db.collection("users").whereEqualTo("username", search).get()
                        .addOnSuccessListener {
                            if (it.documents.size == 1) {
                                if (it.documents[0]["id"].toString() != Firebase.auth.currentUser?.uid) {
                                    artistsList.add(
                                        User(it.documents[0]["id"].toString())
                                    )
                                    mRecyclerView.adapter?.notifyItemChanged(0)
                                }
                            } else {
                                showMessageShort("More than one or empty result in DB.")
                            }
                            completion()
                        }.addOnFailureListener {
                            Log.d("error", "Query failed")
                            completion()
                        }
                }
            } else {
                db.collection("artists").get().addOnSuccessListener {
                    it.documents.forEachIndexed { index, document ->
                        if (document["id"].toString() != Firebase.auth.currentUser?.uid) {
                            artistsList.add(
                                User(document["id"].toString())
                            )
                            mRecyclerView.adapter?.notifyItemChanged(index)
                        }
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

    private fun resetSearch() {
        search.setQuery("", false)
        search.clearFocus()
        artistsList.clear()
        showRecyclerView(mainView, false)
    }

    override fun onPause() {
        super.onPause()
        resetSearch()
    }

    override fun onResume() {
        super.onResume()
        resetSearch()
    }
}