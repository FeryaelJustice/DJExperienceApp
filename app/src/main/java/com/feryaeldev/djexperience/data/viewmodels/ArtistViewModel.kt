package com.feryaeldev.djexperience.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feryaeldev.djexperience.data.enums.ArtistType
import com.feryaeldev.djexperience.data.models.Artist

class ArtistViewModel : ViewModel() {

    private val artist = MutableLiveData<Artist>()

    init {
        artist.postValue(Artist("",ArtistType.DJPRODUCER,0))
    }

    fun getArtist(): MutableLiveData<Artist> = artist

    fun setArtist(newArtist: Artist) {
        artist.postValue(newArtist)
    }
}