package com.example.mobphotoedit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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