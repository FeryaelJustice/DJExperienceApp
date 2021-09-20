package com.feryaeldev.djexperience.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.feryaeldev.djexperience.ui.base.BaseActivity
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.full.memberProperties


// Object to Map
inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}

fun hasWriteStoragePermission(activity: BaseActivity): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val requestPermissionLauncher =
                activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    if (it) {
                        Log.d("permission", "granted")
                    }
                }
            requestPermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return false
        }
    }

    return true
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