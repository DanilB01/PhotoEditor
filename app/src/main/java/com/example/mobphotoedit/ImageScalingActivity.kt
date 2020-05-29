package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_desktop.photo
import kotlinx.android.synthetic.main.activity_image_scalling.*
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow

class ImageScalingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scalling)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            switchActivity(newUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }

        var curBitmap = imageView2Bitmap(photo)
        val but1: Button = findViewById(R.id.button20)

        but1.setOnClickListener {
            val mykoefstring2 = editText.text.toString()
            val mykoefnorm2 = mykoefstring2.toFloat()
            if(mykoefnorm2 <= 3 && mykoefnorm2 > 0){
                bigpicture(mykoefnorm2, photo, curBitmap )
                Toast.makeText(this, "$mykoefnorm2", Toast.LENGTH_LONG).show()
                curBitmap = imageView2Bitmap(photo)
                curBitmap = checkBitmap(curBitmap, this)
            }
            else{
                Toast.makeText(this, R.string.wrongVal, Toast.LENGTH_LONG).show()
            }
        }
        infoBut.setOnClickListener {
            infoDialog()
        }
    }

    private fun infoDialog() {
        val infoDialog = AlertDialog.Builder(this)
        infoDialog.setTitle(resources.getString(R.string.info))
        infoDialog.setMessage(resources.getString(R.string.infoScaling))
        infoDialog.setPositiveButton(resources.getString(R.string.ok)) {
                dialog, which ->
        }
        infoDialog.show()
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent()
        i.putExtra("newImageUri", imageUri.toString())
        setResult(Activity.RESULT_OK, i)
        finish()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}

private fun imageView2Bitmap(view: ImageView): Bitmap {
    var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}


private fun bigpicture(koef:Float, photo: ImageView, curBitmap: Bitmap){

    val oldh =  curBitmap.height
    val oldw = curBitmap.width
    val newh:Int = (oldh.toFloat()*koef).toInt()
    val neww:Int = (oldw.toFloat()*koef).toInt()


    //  val newh = (oldh*koef).toInt()
    // val neww = (oldw*koef).toInt()
    if (koef<= 1){
        mashtab(koef,curBitmap,photo)
    }
    else{
        /*
    val newh: Int = curBitmap.height
    val neww:Int = curBitmap.width
    val oldh:Int = (newh.toFloat()/koef).toInt()
    val oldw:Int = (neww.toFloat()/koef).toInt()

         */
        var finishbit = Bitmap.createBitmap(neww, newh, Bitmap.Config.ARGB_8888)
        for (i in 0 until newh){
            var tmp = i.toFloat()/((newh-1).toFloat())*(oldh-1)
            var h = floor(tmp).toInt()
            if (h >= oldh - 1)
                h = oldh - 2
            if (h<0)
                h=0
            val u = tmp-h
            for (j in 0 until neww) {
                tmp = j.toFloat()/((neww-1).toFloat())*(oldw-1)
                var w = floor(tmp).toInt()
                if (w >= oldw - 1)
                    w = oldw - 2
                if (w<0)
                    w=0
                val t = tmp-w

                val d1 = (1-t)*(1-u)
                val d2 = t*(1-u)
                val d3 = t*u
                val d4 = (1-t)*u

                val p1 = curBitmap.getPixel(w,h)
                val p2 = curBitmap.getPixel(w+1,h)
                val p3 = curBitmap.getPixel(w,h+1)
                val p4 = curBitmap.getPixel(w+1,h+1)

                val blue = (Color.blue(p1)*d1 + Color.blue(p2)*d2 + Color.blue(p3)*d3 + Color.blue(p4)*d4).toInt()
                val green = (Color.green(p1)*d1 + Color.green(p1)*d2 + Color.green(p3)*d3 + Color.green(p4)*d4).toInt()
                val red = (Color.red(p1)*d1 + Color.red(p1)*d2 + Color.red(p1)*d3 + Color.red(p1)*d4).toInt()
                finishbit.setPixel(j,i, Color.rgb(red, green, blue))

            }
        }
        photo.setImageBitmap(finishbit)


    }
}



private fun downScalingImage(times: Float, photo: ImageView, curBitmap: Bitmap){
    val oldHeight: Int = curBitmap.height
    val oldWidth:Int = curBitmap.width
    val newHeight:Int = (oldHeight.toFloat()/times).toInt()
    val newWidth:Int = (oldWidth.toFloat()/times).toInt()
    val newBitmap = Bitmap.createBitmap(newHeight, newWidth, Bitmap.Config.ARGB_8888)
    for (j in 0 until newWidth){
        for (i in 0 until newHeight){
            val x = (j.toFloat()*times).toInt()
            val y = (i.toFloat()*times).toInt()
            var red:Int = 0
            var blue:Int = 0
            var green:Int = 0
            for (xi in 0 until ceil(times).toInt()) {
                for (yi in 0 until ceil(times).toInt()) {
                    val color = curBitmap.getPixel(x+xi,y+yi)
                    red += Color.red(color)
                    green += Color.green(color)
                    blue += Color.blue(color)
                }
            }
            val r = red/((ceil(times).pow(2)).toInt())
            val g = green/((ceil(times).pow(2)).toInt())
            val b = blue/((ceil(times).pow(2)).toInt())
            val color = Color.rgb(r,g,b)
            newBitmap.setPixel(j,i,color)
        }
    }
    photo.setImageBitmap(newBitmap)

}

private fun mashtab(koef: Float, bmap: Bitmap, photo: ImageView) {
/*
    val aspectRatio: Float = bmap.height.toFloat() / bmap.width
    val displayMetrics  = DisplayMetrics()
      //  resources.displayMetrics
    val mImageWidth = displayMetrics.widthPixels
    val mImageHeight = (mImageWidth * aspectRatio).roundToInt()
    val mBitmap = Bitmap.createScaledBitmap(bmap, mImageWidth, mImageHeight, false)

 */

    val nWidth: Int = (bmap.width * koef).toInt()
    val nHeight: Int = (bmap.height * koef).toInt()
    val bmp: Bitmap = Bitmap.createBitmap(nWidth, nHeight,  Bitmap.Config.ARGB_8888)

    for (y in 0 until nHeight)
        for (x in 0 until nWidth) {
            val r = bmap.getPixel((x / koef).toInt(), (y/koef).toInt())
            bmp.setPixel(x, y, r)
        }
    photo.setImageBitmap(bmp);

}