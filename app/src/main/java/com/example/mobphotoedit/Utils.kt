package com.example.mobphotoedit

import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import java.io.*
import java.util.*
import java.util.function.DoubleToLongFunction


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

/*
fun getDecodedBitmap(b_p: Bitmap): Bitmap? {
    val stream = ByteArrayOutputStream()
    var new_b_p = b_p.copy(b_p.config,true)
    new_b_p.compress(Bitmap.CompressFormat.PNG,50,stream)
    val byteArray: ByteArray = stream.toByteArray()
    val compressedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
   return compressedBitmap
}
*/

// class to work with bitmap history
class BitmapStore {

    private val mHistory: Stack<Bitmap>
    private val mBuffer: Stack<Bitmap>
    private var mOriginalBitmap: Bitmap? = null
    private var mCounter = 0

    fun addBitmap(bitmap: Bitmap) {
        mHistory.push(bitmap.copy(Bitmap.Config.ARGB_8888, true))
        mCounter += 1
        if (mCounter == STACK_SIZE) dropStack()
        mBuffer.clear()
    }

    // put current bitmap in mBuffer
    fun popBitmap(): Bitmap? {
        val bitmap: Bitmap
        bitmap = try {
            mHistory.pop() // if mHistory is empty
        } // return original
        catch (e: EmptyStackException) {
            return mOriginalBitmap
        }
        mBuffer.push(bitmap)
        mCounter -= 1
        return bitmap
    }

    fun takeFromBuffer(): Bitmap? {
        val bitmap: Bitmap
        bitmap = try {
            mBuffer.pop()
        } catch (e: EmptyStackException) {
            return null
        }
        mHistory.push(bitmap.copy(Bitmap.Config.ARGB_8888, true))
        mCounter += 1
        if (mCounter == STACK_SIZE) dropStack()
        return bitmap
    }

    fun showHead(): Bitmap? {
        val bitmap: Bitmap
        bitmap = try {
            mHistory.peek()
        } catch (e: EmptyStackException) {
            return mOriginalBitmap
        }
        return bitmap
    }

    fun clearAllAndSetOriginal(bitmap: Bitmap) {
        mHistory.clear()
        mBuffer.clear()
        mOriginalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        mCounter = 1
    }

    private fun dropStack() {
        mOriginalBitmap = mHistory.removeAt(0)
        mCounter -= 1
    }

    companion object {
        private const val STACK_SIZE = 9
    }

    init {
        mHistory = Stack()
        mBuffer = Stack()
    }
}
