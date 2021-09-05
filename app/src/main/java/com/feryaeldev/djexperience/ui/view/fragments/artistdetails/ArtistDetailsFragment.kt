package com.feryaeldev.djexperience.ui.view.fragments.artistdetails

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
import com.feryaeldev.djexperience.ui.base.BaseFragment
import com.feryaeldev.djexperience.data.model.domain.Artist
import com.feryaeldev.djexperience.data.model.domain.User
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream

class ArtistDetailsFragment : BaseFragment() {

    private lateinit var progressCircle: FragmentContainerView

    // Media
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseBtn: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist_details, container, false)

        val progressCircle = view.findViewById<FragmentContainerView>(R.id.artistdetails_fragmentProgressBar)
        progressCircle.visibility = View.VISIBLE
        val artistData = view.findViewById<LinearLayout>(R.id.artistdetails_data)
        artistData.visibility = View.GONE

        // VITAL VARIABLES
        val arguments = arguments
        val userId = Firebase.auth.currentUser?.uid
        val artistId = arguments?.getString("id")

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

        val db = Firebase.firestore

        // Get artist data
        artistId?.let { id ->
            db.collection("artists").document(id).get().addOnSuccessListener { documentSnap ->
                val artist = Artist(
                    documentSnap["id"].toString(),
                    documentSnap["name"].toString(),
                    documentSnap["surnames"].toString(),
                    documentSnap["username"].toString(),
                    documentSnap["email"].toString(),
                    documentSnap["country"].toString(),
                    documentSnap["category"].toString(),
                    documentSnap["age"].toString().toInt(),
                    documentSnap["website"].toString()
                )
                username.text = artist.username
                name.text = artist.name
                surnames.text = artist.surnames
                country.text = artist.country
                category.text = artist.category
                age.text = resources.getString(R.string.age, artist.age.toString())
                websiteUrl = artist.website.toString()

                progressCircle.visibility = View.GONE
                artistData.visibility = View.VISIBLE
            }
            val profilePicRef =
                Firebase.storage.reference.child("profile_images/${id}.jpg")
            val tempFile = File.createTempFile("tempImage", "jpg")
            profilePicRef.getFile(tempFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                image.setImageBitmap(bitmap)
            }
            tempFile.delete()
        }

        val docRef = userId?.let { db.collection("users").document(it) }

        // Update button if following artist
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                val user: User? = document.toObject(User::class.java)
                var found = false
                user?.following?.forEach {
                    if (it == artistId) {
                        found = true
                    }
                }
                if (found) {
                    addRemoveToProfile.setImageResource(R.drawable.ic_baseline_remove_24)
                    addRemoveToProfile.tag = R.drawable.ic_baseline_remove_24
                    addRemoveToProfileText.text = view.resources.getString(R.string.sustract)
                } else {
                    addRemoveToProfile.setImageResource(R.drawable.ic_baseline_add_24)
                    addRemoveToProfile.tag = R.drawable.ic_baseline_add_24
                    addRemoveToProfileText.text = view.resources.getString(R.string.add)
                }
            }
        }

        // Image go to website
        image.setOnClickListener {
            if (websiteUrl != "") {
                try {
                    val intentURL = Intent(Intent.ACTION_VIEW)
                    intentURL.data = Uri.parse("http://$websiteUrl")
                    startActivity(intentURL)
                } catch (e: Exception) {
                    showMessageShort("Error trying to open website of the artist: $e")
                }
            }
        }

        // Add or remove artist from following on user logic
        addRemoveToProfile.setOnClickListener {
            docRef?.get()?.addOnSuccessListener { document ->
                if (document != null) {
                    val user: User? = document.toObject(User::class.java)
                    // Push or substract artist
                    if (artistId != null) {
                        if (addRemoveToProfile.tag == R.drawable.ic_baseline_add_24) {
                            user?.following?.add(artistId)
                            addRemoveToProfile.setImageResource(R.drawable.ic_baseline_remove_24)
                            addRemoveToProfile.tag = R.drawable.ic_baseline_remove_24
                            addRemoveToProfileText.text =
                                view.resources.getString(R.string.sustract)
                        } else {
                            val tempList = arrayListOf<String>()
                            user?.following?.let { it1 -> tempList.addAll(it1) }
                            user?.following?.forEach {
                                if (it == artistId) {
                                    tempList.remove(it)
                                }
                            }
                            user?.following = tempList
                            addRemoveToProfile.setImageResource(R.drawable.ic_baseline_add_24)
                            addRemoveToProfile.tag = R.drawable.ic_baseline_add_24
                            addRemoveToProfileText.text = view.resources.getString(R.string.add)
                        }
                    }
                    // Update
                    if (user != null) {
                        db.collection("users").document(userId).set(user)
                    }
                }
            }
        }

        // Media demo track

        // Instances

        seekBar = view.findViewById(R.id.seekbarDemoTrack)
        playPauseBtn = view.findViewById(R.id.media_play_btn_demoTrack)
        val mediaTitle = view.findViewById<TextView>(R.id.media_demoTrack_title)

        // LOCAL
        // Get song:
        // mediaPlayer = MediaPlayer.create(view.context, R.raw.headhunterzorangeheartextended)
        // Initialize
        /*
        seekBar.progress = 0
        mediaPlayer.seekTo(0)
        seekBar.max = mediaPlayer.duration
        */

        // REMOTE
        // Init mediaplayer:
        mediaPlayer = MediaPlayer()
        mediaPlayer.reset()
        // Get track reference
        val demoSongRef = Firebase.storage.reference.child("songs/demo/${artistId}.mp3")
        // Temp file
        val tempFile = File.createTempFile("temp_$artistId", ".mp3")
        tempFile.deleteOnExit()
        // Download track
        demoSongRef.getFile(tempFile).addOnSuccessListener {
            Log.d("download", "success")

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
                    artistId?.let { id ->
                        val idUppercaseFirstLetter = id.replaceFirstChar { it.uppercase() }
                        mediaTitle.text = getString(R.string.artist_demo, idUppercaseFirstLetter)
                    }

                    // Autoplay
                    startMediaPlayer()
                }
            } catch (e: Error) {
                showMessageShort("Error: $e")
            }
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

        // CLOSE BUTTON
        view.findViewById<ImageView>(R.id.fragment_artist_details_close).setOnClickListener {
            resetMediaPlayer()
            findNavController().popBackStack()
        }

        findNavController().addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.artistDetailsFragment) {
                resetMediaPlayer()
            }
        }

        return view
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