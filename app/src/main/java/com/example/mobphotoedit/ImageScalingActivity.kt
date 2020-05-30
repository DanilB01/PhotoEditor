package com.example.mobphotoedit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_scalling.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow


class ImageScalingActivity : AppCompatActivity() {
    private var imageUriUri: Uri? = null
    private var isChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scalling)

        pBar.visibility = View.GONE
        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        imageUriUri = imageUri
        photo.setImageURI(imageUri)
        var b_p = imageView2Bitmap(photo)

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            bitmapStore.addBitmap(imageView2Bitmap(photo))
            switchActivity(newUri)
        }
        no.setOnClickListener {
            if(isChanged)
                quitDialog()
            else
                switchActivity(imageUri)
        }

        var curBitmap = imageView2Bitmap(photo)
        val but1: Button = findViewById(R.id.button20)
        //bigpicture(mykoefnorm2, photo, curBitmap)
        but1.setOnClickListener {
            isChanged = true
            val mykoefstring2 = editText.text.toString()
            val mykoefnorm2 = mykoefstring2.toFloat()
            if(mykoefnorm2 <= 3 && mykoefnorm2 > 0){
                pBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Default).async {
                    val b_m = bigpicture(mykoefnorm2, photo, curBitmap)
                    launch(Dispatchers.Main) {
                        photo.setImageBitmap(b_m)
                        curBitmap = imageView2Bitmap(photo)
                        pBar.visibility = View.GONE }
                }
                Toast.makeText(this, "$mykoefnorm2", Toast.LENGTH_LONG).show()
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

    private fun quitDialog() {
        val quitDialog = AlertDialog.Builder(this)
        quitDialog.setTitle(resources.getString(R.string.leave))
        quitDialog.setPositiveButton(resources.getString(R.string.yes)) {
                dialog, which -> switchActivity(imageUriUri!!)
        }
        quitDialog.setNegativeButton(resources.getString(R.string.no)){
                dialog, which ->

        }
        quitDialog.show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isChanged)
                quitDialog()
            else
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


private fun bigpicture(koef:Float, photo: ImageView, curBitmap: Bitmap): Bitmap{

    val oldh =  curBitmap.height
    val oldw = curBitmap.width
    val newh:Int = (oldh.toFloat()*koef).toInt()
    val neww:Int = (oldw.toFloat()*koef).toInt()


    //  val newh = (oldh*koef).toInt()
    // val neww = (oldw*koef).toInt()
    if (koef<= 1){
        return mashtab(koef,curBitmap,photo)
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
        return finishbit
        //photo.setImageBitmap(finishbit)

    }
}


private fun mashtab(koef: Float, bmap: Bitmap, photo: ImageView): Bitmap {
    val nWidth: Int = (bmap.width * koef).toInt()
    val nHeight: Int = (bmap.height * koef).toInt()
    val bmp: Bitmap = Bitmap.createBitmap(nWidth, nHeight,  Bitmap.Config.ARGB_8888)

    for (y in 0 until nHeight)
        for (x in 0 until nWidth) {
            val r = bmap.getPixel((x / koef).toInt(), (y/koef).toInt())
            bmp.setPixel(x, y, r)
        }
    return bmp
}