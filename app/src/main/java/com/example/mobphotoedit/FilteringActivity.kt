package com.example.mobphotoedit


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable

import android.net.Uri
import android.os.Bundle

//import kotlinx.android.synthetic.main.activity_desktop.*
//import kotlinx.android.synthetic.main.activity_main.*

import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_filtering.*


class FilteringActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)


        yes.setOnClickListener {
            switchActivity(imageUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
       val itsmybit = imageView2Bitmap(photo)
        val mykoefstring = editText.text.toString()//я зх почему он тут флоат не разрешает
        val mykoefnorm = mykoefstring.toFloat()


        photo.imageMatrix = setUpScaleType(itsmybit, photo, photo.drawable.intrinsicWidth.toFloat()*mykoefnorm,photo.drawable.intrinsicHeight.toFloat()*mykoefnorm )



        //photo.imageMatrix = Matrix().apply {
           // val dWidth = photo.drawable.intrinsicWidth
           // val dHeight = photo.drawable.intrinsicHeight
           // setScale(0.5f, 0.5f, dWidth/2f, dHeight/2f )
      //  }

        val but1: Button = findViewById(R.id.button)
        but1.setOnClickListener {
            val mykoefstring2 = editText.text.toString()//я зх почему он тут флоат не разрешает
            val mykoefnorm2 = mykoefstring2.toFloat()
            photo.imageMatrix = setUpScaleType(itsmybit, photo, photo.drawable.intrinsicWidth.toFloat()*mykoefnorm2,photo.drawable.intrinsicHeight.toFloat()*mykoefnorm2 )
            Toast.makeText(this, "$mykoefnorm2",Toast.LENGTH_LONG).show()
        }

      //  val editmytext: EditText = findViewById(R.id.editText)
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent(this, DesktopActivity::class.java)
        i.putExtra("ImageUri", imageUri.toString())
        startActivity(i)
    }

}

private fun setUpScaleType(
    bitmap: Bitmap?,
    iv: ImageView,
    width: Float,
    height: Float
): Matrix? {
    var scaleX = 1f
    var scaleY = 1f
    var dx = 0f
    var dy = 0f
    val shaderMatrix = Matrix()
    if (bitmap == null) {
        return null
    }
    shaderMatrix.set(null)
    if (iv.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
        if (width != bitmap.width.toFloat()) {
            scaleX = width / bitmap.width
        }
        if (scaleX * bitmap.height < height) {
            scaleX = height / bitmap.height
        }
        dy = (height - bitmap.height * scaleX) * 0.5f
        dx = (width - bitmap.width * scaleX) * 0.5f
        shaderMatrix.setScale(scaleX, scaleX)
    } else {
        scaleX = width / bitmap.width
        scaleY = height / bitmap.height
        dy = (height - bitmap.height * scaleY) * 0.5f
        dx = (width - bitmap.width * scaleX) * 0.5f
        shaderMatrix.setScale(scaleX, scaleY)
    }
    shaderMatrix.postTranslate(dx + 0.5f, dy + 0.5f)
    return shaderMatrix
}

private fun imageView2Bitmap(view: ImageView ):Bitmap{
    var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}

