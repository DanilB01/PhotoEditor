
package com.example.mobphotoedit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_desktop.*



class LineDrawingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_rotation)

        var path: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(path)


        photo.setImageURI(imageUri)


    }}

/*
import com.example.mobphotoedit.R

https://dpaste.org/L1t9
https://dpaste.org/L1t9
About History New snippet
Kotlin Expires in: 5 days, 15 hours
Delete Now
Slim
package com.example.peiceofshit.EditingTools

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.get
import com.example.peiceofshit.R
import com.example.peiceofshit.RedactingWindow
import com.example.peiceofshit.decodeSampledBitmapFromResource
import kotlinx.android.synthetic.main.activity_zoom.*
import java.io.ByteArrayOutputStream
import java.lang.Math.pow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

private var curBitmap:Bitmap? = null
private var curUri:Uri? = null
private var newBitmap:Bitmap? = null

class Zoom : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)
        val ss:String = intent.getStringExtra("cur_image_uri")
        curUri = Uri.parse(intent.getStringExtra("cur_image_uri"))
        curBitmap = decodeSampledBitmapFromResource(curUri!!, 1024, 1024, this)
        bottom_navigation.visibility = View.INVISIBLE
        photo_for_editing.setImageBitmap(curBitmap)
        scale_up.setOnClickListener {
            done_completely.visibility = View.INVISIBLE
            select_scaling.visibility = View.INVISIBLE
            bottom_navigation.visibility = View.VISIBLE
            percent.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(view: View?, keyCode: Int, keyevent: KeyEvent): Boolean {
                    //If the keyevent is a key-down event on the "enter" button
                    return if (keyevent.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        val scale = percent.text.toString().toFloat()
                        upScalingImage(scale)
                        photo_for_editing.setImageBitmap(newBitmap)
                        true
                    } else false
                }
            })
            doneZoom.setOnClickListener{
                select_scaling.visibility = View.VISIBLE
                bottom_navigation.visibility = View.INVISIBLE
                doneZoom.visibility = View.INVISIBLE
                curBitmap = newBitmap
                curUri = getUriFromBitmap(this)
                done_completely.visibility = View.VISIBLE
            }
        }
        scale_down.setOnClickListener {
            done_completely.visibility = View.INVISIBLE
            select_scaling.visibility = View.INVISIBLE
            bottom_navigation.visibility = View.VISIBLE
            percent.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(view: View?, keyCode: Int, keyevent: KeyEvent): Boolean {
                    //If the keyevent is a key-down event on the "enter" button
                    return if (keyevent.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        val scale = percent.text.toString().toFloat()
                        downScalingImage(scale)
                        photo_for_editing.setImageBitmap(newBitmap)
                        true
                    } else false
                }
            })
            doneZoom.setOnClickListener{
                select_scaling.visibility = View.VISIBLE
                bottom_navigation.visibility = View.INVISIBLE
                doneZoom.visibility = View.INVISIBLE
                curBitmap = newBitmap
                curUri = getUriFromBitmap(this)
                done_completely.visibility = View.VISIBLE
            }
        }
        doneCompletely.setOnClickListener {
            var intent = Intent(this, RedactingWindow::class.java)
            intent.putExtra("image_path",curUri.toString())
            startActivity(intent)
        }
    }
}

private fun getBitmapFromUri(context: Context, uri: Uri?) : Bitmap? {
    return if(Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
    else {
        val source = ImageDecoder.createSource(context.contentResolver, uri!!)
        ImageDecoder.decodeBitmap(source)
    }
}
private fun getUriFromBitmap(context: Context): Uri?{
    val bytes = ByteArrayOutputStream()
    curBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, curBitmap, "Title", null)
    return Uri.parse(path.toString())
}

private fun upScalingImage(times:Float){
    val newHeight: Int = curBitmap!!.height
    val newWidth:Int = curBitmap!!.width
    val oldHeight:Int = (newHeight.toFloat()/times).toInt()
    val oldWidth:Int = (newWidth.toFloat()/times).toInt()
    var tmpBitmap:Bitmap = Bitmap.createBitmap(oldWidth, oldHeight, Bitmap.Config.ARGB_8888)
    val startX = (newWidth - oldWidth)/2
    val startY = (newHeight - oldHeight)/2
    for (y in 0 until oldHeight) {
        for (x in 0 until oldWidth) {
            val oldPixel = curBitmap!!.getPixel(startX+x,startY+y)
            tmpBitmap.setPixel(x,y,oldPixel)
        }
    }
    newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        var tmp = j.toFloat()/((newHeight-1).toFloat())*(oldHeight-1)
        var x = floor(tmp).toInt()
        if (x >= oldHeight - 1)
            x = oldHeight - 2
        val u = tmp-x
        for (i in 0 until newWidth) {
            tmp = i.toFloat()/((newWidth-1).toFloat())*(oldWidth-1)
            var y = floor(tmp).toInt()
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
}

private fun downScalingImage(times: Float){
    val oldHeight: Int = curBitmap!!.height
    val oldWidth:Int = curBitmap!!.width
    val newHeight:Int = (oldHeight.toFloat()/times).toInt()
    val newWidth:Int = (oldWidth.toFloat()/times).toInt()
    newBitmap = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        for (i in 0 until newWidth){
            val x = (j.toFloat()*times).toInt()
            val y = (i.toFloat()*times).toInt()
            var red:Int = 0
            var blue:Int = 0
            var green:Int = 0
            for (xi in 0 until ceil(times).toInt()) {
                for (yi in 0 until ceil(times).toInt()) {
                    val color = curBitmap!!.getPixel(x+xi,y+yi)
                    red += Color.red(color)
                    green += Color.green(color)
                    blue += Color.blue(color)
                }
            }
            val r = red/((ceil(times).pow(2)).toInt())
            val g = green/((ceil(times).pow(2)).toInt())
            val b = blue/((ceil(times).pow(2)).toInt())
            val color = Color.rgb(r,g,b)
            newBitmap!!.setPixel(j,i,color)
        }
    }
}
Copy Snippet
Edit Snippet
Wordwrap
package com.example.peiceofshit.EditingTools
​
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.get
import com.example.peiceofshit.R
import com.example.peiceofshit.RedactingWindow
import com.example.peiceofshit.decodeSampledBitmapFromResource
import kotlinx.android.synthetic.main.activity_zoom.*
import java.io.ByteArrayOutputStream
import java.lang.Math.pow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
​
private var curBitmap:Bitmap? = null
private var curUri:Uri? = null
private var newBitmap:Bitmap? = null
​
class Zoom : AppCompatActivity() {
    ​
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zoom)
        val ss:String = intent.getStringExtra("cur_image_uri")
        curUri = Uri.parse(intent.getStringExtra("cur_image_uri"))
        curBitmap = decodeSampledBitmapFromResource(curUri!!, 1024, 1024, this)
        bottom_navigation.visibility = View.INVISIBLE
        photo_for_editing.setImageBitmap(curBitmap)
        scale_up.setOnClickListener {
            done_completely.visibility = View.INVISIBLE
            select_scaling.visibility = View.INVISIBLE
            bottom_navigation.visibility = View.VISIBLE
            percent.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(view: View?, keyCode: Int, keyevent: KeyEvent): Boolean {
                    //If the keyevent is a key-down event on the "enter" button
                    return if (keyevent.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        val scale = percent.text.toString().toFloat()
                        upScalingImage(scale)
                        photo_for_editing.setImageBitmap(newBitmap)
                        true
                    } else false
                }
            })
            doneZoom.setOnClickListener{
                select_scaling.visibility = View.VISIBLE
                bottom_navigation.visibility = View.INVISIBLE
                doneZoom.visibility = View.INVISIBLE
                curBitmap = newBitmap
                curUri = getUriFromBitmap(this)
                done_completely.visibility = View.VISIBLE
            }
        }
        scale_down.setOnClickListener {
            done_completely.visibility = View.INVISIBLE
            select_scaling.visibility = View.INVISIBLE
            bottom_navigation.visibility = View.VISIBLE
            percent.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(view: View?, keyCode: Int, keyevent: KeyEvent): Boolean {
                    //If the keyevent is a key-down event on the "enter" button
                    return if (keyevent.getAction() === KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        val scale = percent.text.toString().toFloat()
                        downScalingImage(scale)
                        photo_for_editing.setImageBitmap(newBitmap)
                        true
                    } else false
                }
            })
            doneZoom.setOnClickListener{
                select_scaling.visibility = View.VISIBLE
                bottom_navigation.visibility = View.INVISIBLE
                doneZoom.visibility = View.INVISIBLE
                curBitmap = newBitmap
                curUri = getUriFromBitmap(this)
                done_completely.visibility = View.VISIBLE
            }
        }
        doneCompletely.setOnClickListener {
            var intent = Intent(this, RedactingWindow::class.java)
            intent.putExtra("image_path",curUri.toString())
            startActivity(intent)
        }
    }
}
​
private fun getBitmapFromUri(context: Context, uri: Uri?) : Bitmap? {
    return if(Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
    else {
        val source = ImageDecoder.createSource(context.contentResolver, uri!!)
        ImageDecoder.decodeBitmap(source)
    }
}
private fun getUriFromBitmap(context: Context): Uri?{
    val bytes = ByteArrayOutputStream()
    curBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path = MediaStore.Images.Media.insertImage(context.contentResolver, curBitmap, "Title", null)
    return Uri.parse(path.toString())
}
​
private fun upScalingImage(times:Float){
    val newHeight: Int = curBitmap!!.height
    val newWidth:Int = curBitmap!!.width
    val oldHeight:Int = (newHeight.toFloat()/times).toInt()
    val oldWidth:Int = (newWidth.toFloat()/times).toInt()
    var tmpBitmap:Bitmap = Bitmap.createBitmap(oldWidth, oldHeight, Bitmap.Config.ARGB_8888)
    val startX = (newWidth - oldWidth)/2
    val startY = (newHeight - oldHeight)/2
    for (y in 0 until oldHeight) {
        for (x in 0 until oldWidth) {
            val oldPixel = curBitmap!!.getPixel(startX+x,startY+y)
            tmpBitmap.setPixel(x,y,oldPixel)
        }
    }
    newBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        var tmp = j.toFloat()/((newHeight-1).toFloat())*(oldHeight-1)
        var x = floor(tmp).toInt()
        if (x >= oldHeight - 1)
            x = oldHeight - 2
        val u = tmp-x
        for (i in 0 until newWidth) {
            tmp = i.toFloat()/((newWidth-1).toFloat())*(oldWidth-1)
            var y = floor(tmp).toInt()
            if (y >= oldWidth - 1)
                y = oldWidth - 2
            val t = tmp-y
            ​
            //коэффициенты
            val d1 = (1-t)*(1-u)
            val d2 = t*(1-u)
            val d3 = t*u
            val d4 = (1-t)*u
            ​
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
            ​
            //компоненты
            val blue = (p1_blue*d1 + p2_blue*d2 +p3_blue*d3 +p4_blue*d4).toInt()
            val green = (p1_green*d1 + p2_green*d2 + p3_green*d3 + p4_green*d4).toInt()
            val red = (p1_red*d1 + p2_red*d2 + p3_red*d3 + p4_red*d4).toInt()
            val color = Color.rgb(red, green, blue)
            newBitmap!!.setPixel(j,i,color)
        }
    }
}
​
private fun downScalingImage(times: Float){
    val oldHeight: Int = curBitmap!!.height
    val oldWidth:Int = curBitmap!!.width
    val newHeight:Int = (oldHeight.toFloat()/times).toInt()
    val newWidth:Int = (oldWidth.toFloat()/times).toInt()
    newBitmap = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.ARGB_8888)
    for (j in 0 until newHeight){
        for (i in 0 until newWidth){
            val x = (j.toFloat()*times).toInt()
            val y = (i.toFloat()*times).toInt()
            var red:Int = 0
            var blue:Int = 0
            var green:Int = 0
            for (xi in 0 until ceil(times).toInt()) {
                for (yi in 0 until ceil(times).toInt()) {
                    val color = curBitmap!!.getPixel(x+xi,y+yi)
                    red += Color.red(color)
                    green += Color.green(color)
                    blue += Color.blue(color)
                }
            }
            val r = red/((ceil(times).pow(2)).toInt())
            val g = green/((ceil(times).pow(2)).toInt())
            val b = blue/((ceil(times).pow(2)).toInt())
            val color = Color.rgb(r,g,b)
            newBitmap!!.setPixel(j,i,color)
        }
    }
}
*/