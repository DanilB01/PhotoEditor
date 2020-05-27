package com.example.mobphotoedit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_desktop.photo
import kotlinx.android.synthetic.main.activity_unsharp_masking.*
import java.lang.Math.abs


class UnsharpMaskingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_masking)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)
        yes.setOnClickListener {
            switchActivity(imageUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
        val kick808 = imageView2Bitmap(photo)
        val imgWidth = kick808.width
        val imgHeight = kick808.height
        /*
        val blurredPixels =
            Array(imgWidth) { IntArray(imgHeight) }
        val boxWidth = 20
        val boxHeight = 20
        val left = 50
        val top = 50
        val right = imgWidth - left
        val bottom = imgHeight - top
        val usmAmount = 0.6F
        val usmThrehold = 3

         */
        val skbar = findViewById<SeekBar>(R.id.seek_bar)
        val skbarrad = findViewById<SeekBar>(R.id.seek_bar2)

        var OnUnsharpChangeListener: SeekBar.OnSeekBarChangeListener = object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val znachsik = skbar.progress.toFloat()
                val rad = skbarrad.progress
                unsharp(kick808,rad,znachsik,3,photo)
                // ReloadImage(skbar,photo,b_p)
                //CheckSize(photo,b_p)
            }
        }
        skbar.setOnSeekBarChangeListener(OnUnsharpChangeListener)
        skbarrad.setOnSeekBarChangeListener(OnUnsharpChangeListener)
        // boxCar(kick808,blurredPixels,left,top,right,bottom,boxWidth,boxHeight)
        //  val skimaskbit = unsharpMask(kick808,blurredPixels,left,top,right,bottom,usmAmount,usmThrehold)
        //  photo.setImageBitmap(skimaskbit)

        //unsharp(kick808, 200,2F,3,photo)

        //  val kick809 = unsharpMask(kick808,50,50,kick808.width-50,kick808.height-50,0.6F,3)
        //  photo.setImageBitmap(kick809)
    }
    private fun switchActivity(imageUri: Uri){
        val i = Intent(UnsharpMaskingActivity@this, DesktopActivity::class.java)
        i.putExtra("ImageUri", imageUri.toString())
        startActivity(i)
    }
}


private fun unsharp(ivPhoto: Bitmap, radius: Int, amount:Float, threshold: Int, photomy : ImageView){
    val blurredPhoto = boxBlur(ivPhoto!!, radius)

    val originalPixels =  Array(ivPhoto!!.width, {IntArray(ivPhoto!!.height)})
    val blurredPixels = Array(blurredPhoto!!.width, {IntArray(blurredPhoto!!.height)})

    for (j in 0 until ivPhoto!!.height) {
        for (i in 0 until ivPhoto!!.width) {
            originalPixels[i][j] = ivPhoto!!.getPixel(i, j)
            blurredPixels[i][j] = blurredPhoto!!.getPixel(i, j)
        }
    }

    unsharpMask(ivPhoto,originalPixels, blurredPixels, amount, threshold, photomy)
}

private fun boxBlur(bitmap: Bitmap, range: Int): Bitmap? {
    assert(range and 1 == 0) { "Range must be odd." }

    val width = bitmap.width
    val height = bitmap.height

    val blurred = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(blurred)

    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    boxBlurHorizontal(pixels, width, height, range / 2)
    boxBlurVertical(pixels, width, height, range / 2)

    canvas.drawBitmap(pixels, 0, width, 0.0f, 0.0f, width, height, true, null)
    return blurred
}


private fun boxBlurHorizontal(pixels: IntArray, w: Int, h: Int, halfRange: Int) {
    var index = 0
    val newColors = IntArray(w)
    for (y in 0 until h) {
        var hits = 0
        var r: Long = 0
        var g: Long = 0
        var b: Long = 0
        for (x in -halfRange until w) {
            val oldPixel = x - halfRange - 1
            if (oldPixel >= 0) {
                val color = pixels[index + oldPixel]
                if (color != 0) {
                    r -= Color.red(color)
                    g -= Color.green(color)
                    b -= Color.blue(color)
                }
                hits--
            }
            val newPixel = x + halfRange
            if (newPixel < w) {
                val color = pixels[index + newPixel]
                if (color != 0) {
                    r += Color.red(color)
                    g += Color.green(color)
                    b += Color.blue(color)
                }
                hits++
            }
            if (x >= 0) {
                newColors[x] = Color.rgb((r / hits).toInt(),
                    (g / hits).toInt(), (b / hits).toInt()
                )
            }
        }
        for (x in 0 until w) {
            pixels[index + x] = newColors[x]
        }
        index += w
    }
}


private fun boxBlurVertical(pixels: IntArray, w: Int, h: Int, halfRange: Int) {
    val newColors = IntArray(h)
    val oldPixelOffset = -(halfRange + 1) * w
    val newPixelOffset = halfRange * w
    for (x in 0 until w) {
        var hits = 0
        var r: Long = 0
        var g: Long = 0
        var b: Long = 0
        var index = -halfRange * w + x
        for (y in -halfRange until h) {
            val oldPixel = y - halfRange - 1
            if (oldPixel >= 0) {
                val color = pixels[index + oldPixelOffset]
                if (color != 0) {
                    r -= Color.red(color)
                    g -= Color.green(color)
                    b -= Color.blue(color)
                }
                hits--
            }
            val newPixel = y + halfRange
            if (newPixel < h) {
                val color = pixels[index + newPixelOffset]
                if (color != 0) {
                    r += Color.red(color)
                    g += Color.green(color)
                    b += Color.blue(color)
                }
                hits++
            }
            if (y >= 0) {
                newColors[y] = Color.rgb((r / hits).toInt(),
                    (g / hits).toInt(), (b / hits).toInt()
                )
            }
            index += w
        }
        for (y in 0 until h) {
            pixels[y * w + x] = newColors[y]
        }
    }
}


private fun unsharpMask(
    ivPhoto: Bitmap,
    origPixels: Array<IntArray>,
    blurredPixels: Array<IntArray>,
    amount: Float,
    threshold: Int,
    photomy: ImageView
) {

    val newBitmap = Bitmap.createBitmap(ivPhoto!!.width, ivPhoto!!.height, Bitmap.Config.ARGB_8888)

    var orgRed = 0
    var orgGreen = 0
    var orgBlue = 0
    var blurredRed = 0
    var blurredGreen = 0
    var blurredBlue = 0
    var usmPixel = 0
    val alpha = -0x1000000
    for (j in 0 until newBitmap.height) {
        for (i in 0 until newBitmap.width) {
            val origPixel = origPixels[i][j]
            val blurredPixel = blurredPixels[i][j]

            orgRed = origPixel shr 16 and 0xff
            orgGreen = origPixel shr 8 and 0xff
            orgBlue = origPixel and 0xff
            blurredRed = blurredPixel shr 16 and 0xff
            blurredGreen = blurredPixel shr 8 and 0xff
            blurredBlue = blurredPixel and 0xff

            if (abs(orgRed - blurredRed) >= threshold) {
                orgRed = (amount * (orgRed - blurredRed) + orgRed).toInt()
                orgRed = if (orgRed > 255) 255 else if (orgRed < 0) 0 else orgRed
            }
            if (abs(orgGreen - blurredGreen) >= threshold) {
                orgGreen = (amount * (orgGreen - blurredGreen) + orgGreen).toInt()
                orgGreen = if (orgGreen > 255) 255 else if (orgGreen < 0) 0 else orgGreen
            }
            if (abs(orgBlue - blurredBlue) >= threshold) {
                orgBlue = (amount * (orgBlue - blurredBlue) + orgBlue).toInt()
                orgBlue = if (orgBlue > 255) 255 else if (orgBlue < 0) 0 else orgBlue
            }
            usmPixel = alpha or (orgRed shl 16) or (orgGreen shl 8) or orgBlue
            newBitmap.setPixel(i, j, usmPixel)
        }
    }
    photomy.setImageBitmap(newBitmap)

    //activity!!.ivPhoto!!.setImageBitmap(newBitmap)
}
private fun imageView2Bitmap(view: ImageView):Bitmap{
    var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}