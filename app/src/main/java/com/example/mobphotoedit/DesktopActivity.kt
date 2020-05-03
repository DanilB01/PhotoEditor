package com.example.mobphotoedit

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import kotlinx.android.synthetic.main.activity_desktop.*


class DesktopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desktop)


        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri: Uri? = string?.toUri()


        photo.setImageURI(imageUri)
    }

}
