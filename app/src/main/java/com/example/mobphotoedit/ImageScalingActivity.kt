package com.example.mobphotoedit


import android.content.Intent

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_scalling.*

//import kotlinx.android.synthetic.main.activity_desktop.*
//import kotlinx.android.synthetic.main.activity_main.*

class ImageScalingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_scalling)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)


        yes.setOnClickListener {
            switchActivity(imageUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
      val itsmyval = imageView2Bitmap(photo)

        //itsmyval?.let()
        //{

        //    photo.setImageBitmap(itsmyval)

       // }




            if(itsmyval!=null){

               // var resizedBitmap = itsmyval.resizeByWidth(100)
               var resizedBitmap= Bitmap.createScaledBitmap(itsmyval,100,100,true)


                photo.setImageBitmap(resizedBitmap)

                toast("Bitmap resized.")
            }else{
                toast("bitmap not found.")
            }

      fun test2(view: View){
            Toast.makeText(this, "test",Toast.LENGTH_LONG).show()
        }
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent(ImageScalingActivity@this, DesktopActivity::class.java)
        i.putExtra("ImageUri", imageUri.toString())
        startActivity(i)
    }

}



private fun imageView2Bitmap(view: ImageView ):Bitmap{
var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}



fun Bitmap.resizeByWidth(width:Int): Bitmap {
    val ratio:Float = this.width.toFloat() / this.height.toFloat()
    val height:Int = Math.round(width / ratio)

    return Bitmap.createScaledBitmap(
        this,
        width,
        height,
        false
    )
}



fun Bitmap.resizeByHeight(height:Int): Bitmap {
    val ratio:Float = this.height.toFloat() / this.width.toFloat()
    val width:Int = Math.round(height / ratio)

    return Bitmap.createScaledBitmap(
        this,
        width,
        height,
        true
    )
}


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
private  fun filter1( bitmap: Bitmap): Bitmap{
    var newbit = bitmap
    dotfilter(newbit)
    return newbit
}



private fun dotfilter(mybitmap: Bitmap){
    var pixels = IntArray(mybitmap.width*mybitmap.height,{0})

    mybitmap.getPixels(pixels,0,mybitmap.width,0,0,mybitmap.width,mybitmap.height)
    for( i in 0..mybitmap.width*mybitmap.height step 2)
        pixels[i]= Color.BLACK
    mybitmap.setPixels(pixels,0,mybitmap.width,0,0,mybitmap.width,mybitmap.height)

}

