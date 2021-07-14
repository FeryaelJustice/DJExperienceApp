package com.feryaeldev.djexperience.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.myDateFormat(): String =
    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault()).format(this)

val Date.formatSize: Int
    get() = this.myDateFormat().length