package com.feryaeldev.djexperience.ui.view.fragments.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.ui.common.ArtistsRecyclerViewAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class ProfileFragment : BaseFragment() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mAdapter: ArtistsRecyclerViewAdapter

    private lateinit var progressCircle: FragmentContainerView
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        progressCircle = view.findViewById(R.id.profile_fragmentProgressBar)
        progressCircle.visibility = View.VISIBLE
        val followingTextView: TextView = view.findViewById(R.id.profile_followingTxt)

        val username: TextView = view.findViewById(R.id.profile_username)
        val category: TextView = view.findViewById(R.id.profile_category)
        val country: TextView = view.findViewById(R.id.profile_country)
        mRecyclerView = view.findViewById(R.id.fragment_profile_recyclerView)
        mRecyclerView.layoutManager = GridLayoutManager(view.context, 3)

        var followingArtistsIds: ArrayList<String>
        val artistsList: MutableList<User> = arrayListOf()

        mAdapter = ArtistsRecyclerViewAdapter(artistsList)
        mRecyclerView.adapter = mAdapter

        val db = Firebase.firestore
        val userOrArtistID = Firebase.auth.currentUser?.uid

        val userDocRef = userOrArtistID?.let { db.collection("users").document(it) }
        val artistDocRef = userOrArtistID?.let { db.collection("artists").document(it) }

        userDocRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                if (document.data?.size != null) {
                    user = document.toObject(User::class.java)
                    username.text = user?.username
                    category.text = user?.category
                    country.text = user?.country
                    followingArtistsIds = user?.following ?: arrayListOf()

                    mRecyclerView.visibility = View.GONE

                    for (id: String in followingArtistsIds) {
                        artistsList.add(User(id))
                    }

                    mAdapter = ArtistsRecyclerViewAdapter(artistsList)
                    mRecyclerView.adapter = mAdapter

                    progressCircle.visibility = View.GONE
                    mRecyclerView.visibility = View.VISIBLE
                    followingTextView.visibility = View.VISIBLE
                } else {
                    Log.d("nodata", "User no such document")

                    artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                        if (documentArtist != null) {
                            user = documentArtist.toObject(User::class.java)
                            username.text = user?.username
                            category.text = user?.category
                            country.text = user?.country
                        } else {
                            Log.d("error", "Artist no such document")
                        }
                    }?.addOnFailureListener { exceptionArtist ->
                        Log.d("error", "Artist get failed with ", exceptionArtist)
                    }

                    progressCircle.visibility = View.GONE
                    mRecyclerView.visibility = View.GONE
                    followingTextView.visibility = View.GONE
                }
            } else {
                Log.d("nodata", "User no such document")

                artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                    if (documentArtist != null) {
                        user = documentArtist.toObject(User::class.java)
                        username.text = user?.username
                        category.text = user?.category
                        country.text = user?.country
                    } else {
                        Log.d("error", "Artist no such document")
                    }
                }?.addOnFailureListener { exceptionArtist ->
                    Log.d("error", "Artist get failed with ", exceptionArtist)
                }

                progressCircle.visibility = View.GONE
                mRecyclerView.visibility = View.GONE
                followingTextView.visibility = View.GONE
            }
        }?.addOnFailureListener { exception ->
            Log.d("error", "User get failed with ", exception)

            artistDocRef?.get()?.addOnSuccessListener { documentArtist ->
                if (documentArtist != null) {
                    user = documentArtist.toObject(User::class.java)
                    username.text = user?.username
                    category.text = user?.category
                    country.text = user?.country
                } else {
                    Log.d("error", "Artist no such document")
                }
            }?.addOnFailureListener { exceptionArtist ->
                Log.d("error", "Artist get failed with ", exceptionArtist)
            }

            progressCircle.visibility = View.GONE
            mRecyclerView.visibility = View.GONE
            followingTextView.visibility = View.GONE
        }

        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${userOrArtistID}.jpg")
        val image: ImageView = view.findViewById(R.id.profile_photo)
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        view.findViewById<Button>(R.id.profile_editProfileBtn).setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_editProfileFragment,
                arguments
            )
        }

        return view
    }
}