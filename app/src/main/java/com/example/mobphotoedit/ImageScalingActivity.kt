package com.example.mobphotoedit

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mobphotoedit.DesktopActivity
import com.example.mobphotoedit.R
import kotlinx.android.synthetic.main.activity_desktop.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.pow

private fun upScalingImage(times:Float, curBitmap:Bitmap):Bitmap{
    val newHeight: Int = curBitmap!!.height
    val newWidth:Int = curBitmap!!.width
    val oldHeight:Int = (newHeight.toFloat()/times).toInt()
    val oldWidth:Int = (newWidth.toFloat()/times).toInt()
    var tmpBitmap: Bitmap = Bitmap.createBitmap(oldWidth, oldHeight, Bitmap.Config.ARGB_8888)
    val startX = (newWidth - oldWidth)/2
    val startY = (newHeight - oldHeight)/2
    for (y in 0 until oldHeight) {
        for (x in 0 until oldWidth) {
            val oldPixel = curBitmap!!.getPixel(startX+x,startY+y)
            tmpBitmap.setPixel(x,y,oldPixel)
        }
    }
    var newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        var tmp = j.toFloat()/((newHeight-1).toFloat())*(oldHeight-1)
        var x = kotlin.math.floor(tmp).toInt()
        if (x >= oldHeight - 1)
            x = oldHeight - 2
        val u = tmp-x
        for (i in 0 until newWidth) {
            tmp = i.toFloat()/((newWidth-1).toFloat())*(oldWidth-1)
            var y = kotlin.math.floor(tmp).toInt()
            if (y >= oldWidth - 1)
                y = oldWidth - 2
            val t = tmp-y
//коэффициенты
            val d1 = (1-t)*(1-u)
            val d2 = t*(1-u)
            val d3 = t*u
            val d4 = (1-t)*u
//окрестные пиксели
            val p1 = tmpBitmap.getPixel(x,y)
            val p1_red = Color.red(p1)
            val p1_blue = Color.blue(p1)
            val p1_green = Color.green(p1)
            val p2 = tmpBitmap.getPixel(x+1,y)
            val p2_red = Color.red(p2)
            val p2_blue = Color.blue(p2)
            val p2_green = Color.green(p2)
            val p3 = tmpBitmap.getPixel(x,y+1)
            val p3_red = Color.red(p3)
            val p3_blue = Color.blue(p3)
            val p3_green = Color.green(p3)
            val p4 = tmpBitmap.getPixel(x+1,y+1)
            val p4_red = Color.red(p4)
            val p4_blue = Color.blue(p4)
            val p4_green = Color.green(p4)
//компоненты
            val blue = (p1_blue*d1 + p2_blue*d2 +p3_blue*d3 +p4_blue*d4).toInt()
            val green = (p1_green*d1 + p2_green*d2 + p3_green*d3 + p4_green*d4).toInt()
            val red = (p1_red*d1 + p2_red*d2 + p3_red*d3 + p4_red*d4).toInt()
            val color = Color.rgb(red, green, blue)
            newBitmap!!.setPixel(j,i,color)
        }
    }

    return newBitmap
}

private fun downScalingImage(times: Float, curBitmap: Bitmap):Bitmap
{
    val oldHeight: Int = curBitmap!!.height
    val oldWidth:Int = curBitmap!!.width
    val newHeight:Int = (oldHeight.toFloat()/times).toInt()
    val newWidth:Int = (oldWidth.toFloat()/times).toInt()
    var newBitmap = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        for (i in 0 until newWidth){
            val x = (j.toFloat()*times).toInt()
            val y = (i.toFloat()*times).toInt()
            var red:Int = 0
            var blue:Int = 0
            var green:Int = 0
            for (xi in 0 until kotlin.math.ceil(times).toInt()) {
                for (yi in 0 until kotlin.math.ceil(times).toInt()) {
                    val color = curBitmap!!.getPixel(x+xi,y+yi)
                    red += Color.red(color)
                    green += Color.green(color)
                    blue += Color.blue(color)
                }
            }
            val r = red/((kotlin.math.ceil(times).pow(2)).toInt())
            val g = green/((kotlin.math.ceil(times).pow(2)).toInt())
            val b = blue/((kotlin.math.ceil(times).pow(2)).toInt())
            val color = Color.rgb(r,g,b)
            newBitmap!!.setPixel(j,i,color)
        }
    }
    return newBitmap
}

fun scaleImage(skbar: SeekBar, currentImage: ImageView, b_p: Bitmap)
{
    var times:Float = skbar.getProgress().toFloat()
    currentImage.setImageBitmap(upScalingImage(times,b_p))
}

class ImageScalingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scalling)

        var mTextView = findViewById<TextView>(R.id.Scale_Text);
        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)
        var b_p = (photo.getDrawable() as BitmapDrawable).bitmap

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            switchActivity(newUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
    }
    private fun switchActivity(imageUri: Uri){
        val i = Intent(this, DesktopActivity::class.java)
        i.putExtra("ImageUri", imageUri.toString())
        startActivity(i)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}