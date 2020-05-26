package com.example.mobphotoedit

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

fun saveImageToInternalStorage(currentImage: ImageView, appContext: Context): Uri {
    var reloadBitmap = (currentImage.getDrawable() as BitmapDrawable).bitmap
    // Get the context wrapper instance
    val wrapper = ContextWrapper(appContext)

    // Initializing a new file
    // The bellow line return a directory in internal storage
    var file = wrapper.getDir("images", Context.MODE_PRIVATE)

    // Create a file to save the image
    file = File(file, "${UUID.randomUUID()}.jpg")

    try {
        // Get the file output stream
        val stream: OutputStream = FileOutputStream(file)

        // Compress bitmap
        reloadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // Flush the stream
        stream.flush()

        // Close stream
        stream.close()
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
    }
    // Return the saved image uri
    return Uri.parse(file.absolutePath)
}

