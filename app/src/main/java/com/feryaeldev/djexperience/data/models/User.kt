package com.feryaeldev.djexperience.data.models

// = "" by default NEEDED FOR TO OBJECT FROM FIREBASE DESERIALIZE
data class User(
    var id: String? = "",
    var name: String? = "",
    var surnames: String? = "",
    var nickname: String? = "",
    var email: String? = "",
    var country: String? = "",
    var category: String? = "",
    var age: Long? = 0,
    var website: String? = "",
    var following: ArrayList<String>? = arrayListOf()
)
