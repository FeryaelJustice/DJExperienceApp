package com.feryaeldev.djexperience.util

enum class HttpResponseStatus(val code: Int) {
    SUCCESS(200),
    CLIENT_ERROR(404),
    SERVER_ERROR(500)
}