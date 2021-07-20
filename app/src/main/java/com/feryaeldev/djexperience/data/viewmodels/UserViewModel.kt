package com.feryaeldev.djexperience.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feryaeldev.djexperience.data.models.User

class UserViewModel : ViewModel() {

    private val user = MutableLiveData<User>()

    init {
        user.postValue(User("", "", "", "", "", "", "", 0, "", arrayListOf()))
    }

    fun getUser(): MutableLiveData<User> = user

    fun setUser(newUser: User) {
        user.postValue(newUser)
    }
}