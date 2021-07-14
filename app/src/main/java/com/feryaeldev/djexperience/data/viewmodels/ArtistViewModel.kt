package com.feryaeldev.djexperience.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feryaeldev.djexperience.data.models.Artist

class ArtistViewModel : ViewModel() {

    private val artist = MutableLiveData<Artist>()

    fun setArtist(newArtist: Artist) {
        artist.postValue(newArtist)
    }
}