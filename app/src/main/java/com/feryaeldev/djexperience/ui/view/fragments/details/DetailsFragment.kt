package com.feryaeldev.djexperience.ui.view.fragments.details

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.feryaeldev.djexperience.R
import com.feryaeldev.djexperience.data.model.domain.User
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream

class DetailsFragment : BaseFragment() {

    private lateinit var userOrArtistID: String
    private lateinit var userOrArtist: User
    private lateinit var userId: String // Current user id session auth

    private lateinit var progressCircle: FragmentContainerView
    private lateinit var detailsData: LinearLayout

    // Media
    private lateinit var mediaLayout: LinearLayout
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseBtn: ImageView
    private lateinit var mediaTitle: TextView
    private val handler = Handler(Looper.getMainLooper())

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)

        progressCircle = view.findViewById(R.id.details_fragmentProgressBar)
        detailsData = view.findViewById(R.id.details_data)
        progressCircle.visibility = View.VISIBLE
        detailsData.visibility = View.GONE

        // VITAL VARIABLES

        // Vital data
        val db = Firebase.firestore
        userId = Firebase.auth.currentUser?.uid.toString()
        userOrArtistID = arguments?.getString("id").toString()
        userOrArtist = User()

        // View
        val addRemoveToProfile: FloatingActionButton =
            view.findViewById(R.id.fragment_artist_details_addRemoveToProfile)
        val addRemoveToProfileText: TextView =
            view.findViewById(R.id.fragment_artist_details_addRemoveToProfileText)

        val image: ImageView = view.findViewById(R.id.fragment_artist_details_photo)
        val username: TextView = view.findViewById(R.id.fragment_artist_details_username)
        val name: TextView = view.findViewById(R.id.fragment_artist_details_name)
        val surnames: TextView = view.findViewById(R.id.fragment_artist_details_surnames)
        val country: TextView = view.findViewById(R.id.fragment_artist_details_country)
        val category: TextView = view.findViewById(R.id.fragment_artist_details_category)
        val age: TextView = view.findViewById(R.id.fragment_artist_details_age)
        var websiteUrl = ""

        // Media
        mediaLayout = view.findViewById(R.id.media_layout)
        seekBar = view.findViewById(R.id.seekbarDemoTrack)
        playPauseBtn = view.findViewById(R.id.media_play_btn_demoTrack)
        mediaTitle = view.findViewById(R.id.media_demoTrack_title)
        mediaPlayer = MediaPlayer()
        mediaPlayer.reset()

        // Get artist data
        val profilePicRef =
            Firebase.storage.reference.child("profile_images/${userOrArtistID}.jpg")
        val tempFile = File.createTempFile("tempImage", "jpg")
        profilePicRef.getFile(tempFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
            image.setImageBitmap(bitmap)
        }
        tempFile.delete()

        userOrArtistID.let { id ->
            db.collection("artists").document(id).get().addOnSuccessListener { documentSnap ->
                if (documentSnap.data?.get("id") != null) {
                    userOrArtist = User(
                        documentSnap.data?.get("id").toString(),
                        documentSnap.data?.get("name").toString(),
                        documentSnap.data?.get("surnames").toString(),
                        documentSnap.data?.get("username").toString(),
                        documentSnap.data?.get("email").toString(),
                        documentSnap.data?.get("country").toString(),
                        documentSnap.data?.get("category").toString(),
                        documentSnap.data?.get("age").toString().toLongOrNull(),
                        documentSnap.data?.get("website").toString(),
                        documentSnap.data?.get("following") as ArrayList<String>?
                    )
                    username.text = userOrArtist.username
                    name.text = userOrArtist.name
                    surnames.text = userOrArtist.surnames
                    country.text = userOrArtist.country
                    category.text = userOrArtist.category
                    age.text = view.context.resources.getString(
                        R.string.ageInfo,
                        userOrArtist.age.toString()
                    )
                    websiteUrl = userOrArtist.website.toString()

                    progressCircle.visibility = View.GONE
                    detailsData.visibility = View.VISIBLE

                    initMedia()
                }else{
                    db.collection("users").document(id).get().addOnSuccessListener { documentSnaps ->
                        userOrArtist = User(
                            documentSnaps.data?.get("id").toString(),
                            documentSnaps.data?.get("name").toString(),
                            documentSnaps.data?.get("surnames").toString(),
                            documentSnaps.data?.get("username").toString(),
                            documentSnaps.data?.get("email").toString(),
                            documentSnaps.data?.get("country").toString(),
                            documentSnaps.data?.get("category").toString(),
                            documentSnaps.data?.get("age").toString().toLongOrNull(),
                            documentSnaps.data?.get("website").toString(),
                            documentSnaps.data?.get("following") as ArrayList<String>?
                        )
                        username.text = userOrArtist.username
                        name.text = userOrArtist.name
                        surnames.text = userOrArtist.surnames
                        country.text = userOrArtist.country
                        category.text = userOrArtist.category
                        age.text = view.context.resources.getString(
                            R.string.ageInfo,
                            userOrArtist.age.toString()
                        )
                        websiteUrl = userOrArtist.website.toString()

                        progressCircle.visibility = View.GONE
                        detailsData.visibility = View.VISIBLE
                        mediaLayout.visibility = View.GONE
                    }
                }

            }
        }

        // Image go to website
        image.setOnClickListener {
            if (websiteUrl != "") {
                try {
                    val intentURL = Intent(Intent.ACTION_VIEW)
                    if (!websiteUrl.contains("http")) {
                        intentURL.data = Uri.parse("http://$websiteUrl")
                    } else {
                        intentURL.data = Uri.parse(websiteUrl)
                    }
                    startActivity(intentURL)
                } catch (e: Exception) {
                    showMessageShort("Error trying to open website of the artist: $e")
                }
            }
        }

        // Add or remove artist from following on user logic
        val authenticatedUserDbRef = userId.let { db.collection("users").document(it) }
        addRemoveToProfile.setOnClickListener {
            authenticatedUserDbRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val user: User? = document.toObject(User::class.java)
                    // Push or substract artist
                    if (addRemoveToProfile.tag == R.drawable.ic_baseline_add_24) {
                        userOrArtist.id?.let { it1 -> user?.following?.add(it1) }
                        addRemoveToProfile.setImageResource(R.drawable.ic_baseline_remove_24)
                        addRemoveToProfile.tag = R.drawable.ic_baseline_remove_24
                        addRemoveToProfileText.text =
                            view.context.resources.getString(R.string.sustract)
                    } else {
                        val tempList = arrayListOf<String>()
                        user?.following?.let { it1 -> tempList.addAll(it1) }
                        user?.following?.forEach {
                            if (it == userOrArtist.id) {
                                tempList.remove(it)
                            }
                        }
                        user?.following = tempList
                        addRemoveToProfile.setImageResource(R.drawable.ic_baseline_add_24)
                        addRemoveToProfile.tag = R.drawable.ic_baseline_add_24
                        addRemoveToProfileText.text = view.context.resources.getString(R.string.add)
                    }
                    // Update
                    if (user != null) {
                        db.collection("users").document(userId).set(user)
                    }
                }
            }
        }

        // CLOSE BUTTON
        view.findViewById<ImageView>(R.id.fragment_artist_details_close).setOnClickListener {
            resetMediaPlayer()
            findNavController().popBackStack()
        }

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.detailsFragment) {
                resetMediaPlayer()
            }
        }

        return view
    }

    private fun initMedia() {
        val demoSongRef =
            Firebase.storage.reference.child("songs/demo/${userOrArtist.username?.lowercase()}.mp3")
        val tempFile = File.createTempFile("temp_$userOrArtist", ".mp3")
        tempFile.deleteOnExit()
        // Download track
        demoSongRef.getFile(tempFile).addOnSuccessListener {
            Log.d("download", "success")
            mediaLayout.visibility = View.VISIBLE

            // Configure media player
            try {
                val fis = FileInputStream(tempFile)
                mediaPlayer.setDataSource(fis.fd)
                mediaPlayer.prepareAsync()

                // On load
                mediaPlayer.setOnPreparedListener {

                    // Initialize
                    seekBar.progress = 0
                    mediaPlayer.seekTo(0)
                    seekBar.max = mediaPlayer.duration

                    // When music finishes, seekbar position to 0 and button image change
                    mediaPlayer.setOnCompletionListener {
                        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                        seekBar.progress = 0
                        mediaPlayer.seekTo(0)
                    }

                    // Change displayed song name
                    val usernameUppercase =
                        userOrArtist.username.toString().replaceFirstChar { it.uppercase() }
                    mediaTitle.text =
                        view?.context?.getString(R.string.artist_demo, usernameUppercase) ?: ""

                    // Autoplay
                    startMediaPlayer()
                }
            } catch (e: Error) {
                showMessageShort("Error: $e")
                mediaLayout.visibility = View.GONE
            }
        }.addOnFailureListener {
            mediaLayout.visibility = View.GONE
        }

        // Play pause button
        playPauseBtn.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                pauseMediaPlayer()
            } else {
                startMediaPlayer()
            }
        }

        // Jump into user selection
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, position: Int, changed: Boolean) {
                if (changed) {
                    mediaPlayer.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

        // Update seekbar to media position
        activity?.runOnUiThread(object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun startMediaPlayer() {
        mediaPlayer.start()
        playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    private fun resetMediaPlayer() {
        mediaPlayer.stop()
        seekBar.progress = 0
        playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    override fun onStart() {
        super.onStart()
        startMediaPlayer()
    }

    override fun onResume() {
        super.onResume()
        startMediaPlayer()
    }

    override fun onPause() {
        super.onPause()
        pauseMediaPlayer()
    }

    override fun onStop() {
        super.onStop()
        pauseMediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetMediaPlayer()
    }
}