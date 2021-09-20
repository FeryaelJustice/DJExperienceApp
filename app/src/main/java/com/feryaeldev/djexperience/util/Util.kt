package com.feryaeldev.djexperience.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import kotlin.reflect.full.memberProperties


// Object to Map
inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
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
        val outFile = File(dir,filename)

        try {
            outputStream = FileOutputStream(outFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
            outputStream.flush()
            outputStream.close()
            Toast.makeText(applicationContext,"Image saved successfully!",Toast.LENGTH_SHORT).show();
        }catch (e:Error){
            e.printStackTrace()
        }
    }
}