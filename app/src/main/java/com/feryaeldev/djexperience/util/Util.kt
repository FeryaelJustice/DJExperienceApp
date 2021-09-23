package com.feryaeldev.djexperience.util

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.reflect.full.memberProperties

// GLOBAL VALUES
const val djExperienceWebsite =
    "https://feryaeljustice.notion.site/DJ-Experience-d4d4928723af4250a258ac638d7f09a0"
const val authorYoutube = "https://www.youtube.com/channel/UCDYgeJeEqB9tMCE0EqcXE3Q"
const val authorInstagram = "https://www.instagram.com/feryaeljustice"

// Object to Map
inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}

fun checkPermissions(
    permissions: Array<String>,
    launcher: ActivityResultLauncher<Array<String>>,
    context: Context
): Boolean {
    launcher.launch(permissions)
    val permissionsLength = permissions.size
    var counterPermissions = 0
    permissions.forEach {
        if (ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            counterPermissions += 1
        }
    }
    return counterPermissions == permissionsLength
}

fun checkPermission(
    permission: String,
    launcher: ActivityResultLauncher<Any>,
    context: Context
): Boolean {
    launcher.launch(permission)
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

// Method to save an image to internal storage
fun saveImageToInternalStorage(applicationContext: Context, drawableId: Int) {
    val drawable = ContextCompat.getDrawable(applicationContext, drawableId)
    // Get the bitmap from drawable object
    val bitmap = (drawable as BitmapDrawable).bitmap
    var outputStream: FileOutputStream
    val filePath = applicationContext.getExternalFilesDir(null)?.absolutePath
    filePath?.let { filePathNotNull ->
        val dir = File(filePathNotNull)
        //dir.mkdirs()
        val filename = String.format("djexperiencelogo.jpg")
        val outFile = File(dir, filename)
        try {
            outputStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(applicationContext, "Image saved successfully!", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Error) {
            e.printStackTrace()
        }
    }
}

// Check if country has the correct format (Locale available locales list format).
fun countryIsValid(country: String): Boolean {
    var found = false
    val locales = Locale.getAvailableLocales()
    locales.forEach loop@{
        if (country == it.displayName) {
            found = true
            return@loop
        }
    }
    return found
}