package com.example.mobphotoedit

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.renderscript.*
import android.widget.ImageView
import android.widget.Toast
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

class Pixel(val x: Float, val y: Float, val color: Int) {
}

// class to work with bitmap history
class BitmapStore {

    private val mHistory: Stack<Bitmap>
    private var mOriginalBitmap: Bitmap? = null
    private var mCounter = 0

    fun addBitmap(bitmap: Bitmap) {
        mHistory.push(bitmap.copy(Bitmap.Config.ARGB_8888, true))
        mCounter += 1
        if (mCounter == STACK_SIZE) dropStack()
    }

  //get top
    fun popBitmap(): Bitmap? {
        var bitmap: Bitmap
        bitmap = try {
            bitmap = mHistory.peek()
            mHistory.pop() // if mHistory is empty
        } // return original
        catch (e: EmptyStackException) {
            return mOriginalBitmap
        }
        mCounter -= 1
        return bitmap
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
    }
}

fun checkBitmap(b_p: Bitmap, context: Context):Bitmap
{
    var ch_bp = b_p.copy(b_p.config,true)
    if (b_p.byteCount > 10000000)
    {
        ch_bp = resizeBitmap2(ch_bp,ch_bp.width/2, context)
        val toast = Toast.makeText(context, "Sorry, dude! But we use Resize to work with your image!", Toast.LENGTH_SHORT).show()
    }
    return ch_bp
}

//BigImageProcessing
fun resizeBitmap2(src: Bitmap, dstWidth: Int, context: Context): Bitmap? {
    val rs = RenderScript.create(context)
    val bitmapConfig = src.config
    val srcWidth = src.width
    val srcHeight = src.height
    val srcAspectRatio = srcWidth.toFloat() / srcHeight
    val dstHeight = (dstWidth / srcAspectRatio).toInt()
    val resizeRatio = srcWidth.toFloat() / dstWidth

    /* Calculate gaussian's radius */
    val sigma = resizeRatio / Math.PI.toFloat()
    // https://android.googlesource.com/platform/frameworks/rs/+/master/cpu_ref/rsCpuIntrinsicBlur.cpp
    var radius = 2.5f * sigma - 1.5f
    radius = Math.min(25f, Math.max(0.0001f, radius))

    /* Gaussian filter */
    val tmpIn = Allocation.createFromBitmap(rs, src)
    val tmpFiltered = Allocation.createTyped(rs, tmpIn.type)
    val blurInstrinsic = ScriptIntrinsicBlur.create(rs, tmpIn.element)
    blurInstrinsic.setRadius(radius)
    blurInstrinsic.setInput(tmpIn)
    blurInstrinsic.forEach(tmpFiltered)
    tmpIn.destroy()
    blurInstrinsic.destroy()

    /* Resize */
    val dst = Bitmap.createBitmap(dstWidth, dstHeight, bitmapConfig)
    val t: Type = Type.createXY(rs, tmpFiltered.element, dstWidth, dstHeight)
    val tmpOut = Allocation.createTyped(rs, t)
    val resizeIntrinsic = ScriptIntrinsicResize.create(rs)
    resizeIntrinsic.setInput(tmpFiltered)
    resizeIntrinsic.forEach_bicubic(tmpOut)
    tmpOut.copyTo(dst)
    tmpFiltered.destroy()
    tmpOut.destroy()
    resizeIntrinsic.destroy()
    return dst
}

fun imageView2Bitmap(view: ImageView):Bitmap{
    var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}