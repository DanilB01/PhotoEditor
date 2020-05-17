package com.example.mobphotoedit


import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.content.Intent
import android.view.View

import kotlinx.android.synthetic.main.activity_desktop.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.cos
import java.lang.Math.sin

fun ReloadImage(skbar:SeekBar, photo_image_view: ImageView, b_p:Bitmap)
{
    val degrees: Float = (skbar.getProgress()-180).toFloat()
    var rads: Double = (degrees*Math.PI) / 180.0
    val r_matrix = Matrix()
    val src_W:Int = b_p.getWidth()
    val src_H:Int = b_p.getHeight()

    var rotate_arr:FloatArray = floatArrayOf(cos(rads).toFloat(), -sin(rads).toFloat(),src_W.toFloat() / 2,
        sin(rads).toFloat(), cos(rads).toFloat(), src_H.toFloat() / 2,
        0.0f, 0.0f, 1.0f)
    r_matrix.setValues(rotate_arr)
    var new_b_p: Bitmap = Bitmap.createBitmap(
        b_p,
        0, 0,
        src_W, src_H,
        r_matrix, false
    )
    photo_image_view.setImageBitmap(new_b_p)
}

class ImageRotationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_rotation)

        var mTextView = findViewById<TextView>(R.id.Angle_Text);
        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)

        val skbar = findViewById<SeekBar>(R.id.seekBar)
        photo.setImageURI(imageUri)

        var b_p = (photo.getDrawable() as BitmapDrawable).bitmap

        var OnRotateChangeListener: SeekBar.OnSeekBarChangeListener = object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mTextView.setText("Angle: ${(skbar.getProgress().toFloat() - 180).toString()}°")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                ReloadImage(skbar,photo,b_p)
            }
        }
        skbar.setOnSeekBarChangeListener(OnRotateChangeListener)
        ReloadImage(skbar,photo,b_p)


        yes.setOnClickListener {
            switchActivity(imageUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
    }

    private fun switchActivity(imageUri: Uri){
        val i = Intent(ImageRotationActivity@this, DesktopActivity::class.java)
        i.putExtra("ImageUri", imageUri.toString())
        startActivity(i)

    }
}
