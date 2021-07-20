package com.feryaeldev.djexperience.data.models

data class Artist(
    var id: String,
    var name: String,
    var surnames: String,
    var nickname: String,
    var email: String,
    var country: String,
    var category: String,
    var age: Int,
    var website: String
) {
    // NEEDED FOR TOOBJECT FROM FIREBASE DESERIALIZE
    constructor() : this("", "", "", "", "", "", "", 0, "")
}
