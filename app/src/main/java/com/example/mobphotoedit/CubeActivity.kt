package com.example.mobphotoedit

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_desktop.*
import kotlinx.android.synthetic.main.activity_main.*

class CubeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_rotation)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)

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
