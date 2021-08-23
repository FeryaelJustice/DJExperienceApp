package com.feryaeldev.djexperience.model.domain

// = "" by default NEEDED FOR TO OBJECT FROM FIREBASE DESERIALIZE
data class Artist(
    var id: String? = "",
    var name: String? = "",
    var surnames: String? = "",
    var nickname: String? = "",
    var email: String? = "",
    var country: String? = "",
    var category: String? = "",
    var age: Int? = 0,
    var website: String? = ""
)
