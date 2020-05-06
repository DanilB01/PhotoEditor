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
import kotlinx.android.synthetic.main.activity_desktop.*

fun ReloadImage(skbar:SeekBar, image1: ImageView, b_p:Bitmap)
{
    val degrees: Float = (skbar.getProgress() - 180).toFloat()
    var radDegrees: Double = (degrees  * Math.PI) / 180.0
    val matrix = Matrix()
    var drawable_w = image1.drawable.intrinsicWidth
    var drawable_h = image1.drawable.intrinsicHeight

    var mRotated_w = (Math.abs(Math.sin(radDegrees)) * drawable_h
            + Math.abs(Math.cos(radDegrees)) * drawable_w);
    var mRotated_h = (Math.abs(Math.cos(radDegrees)) * drawable_h
            + Math.abs(Math.sin(radDegrees)) * drawable_w);



    matrix.postRotate(
        degrees,
        b_p.getWidth().toFloat() / 2, b_p.getHeight().toFloat() / 2
    )
    var new_b_p: Bitmap = Bitmap.createBitmap(
        b_p,
        0, 0,
        b_p.getWidth(), b_p.getHeight(),
        matrix, true
    )
    image1.setImageBitmap(new_b_p)
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
    }
}

