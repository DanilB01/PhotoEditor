package com.example.mobphotoedit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_desktop.*
import kotlinx.android.synthetic.main.activity_main.*

class RetouchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)
        val skbar_blur_radius = findViewById<SeekBar>(R.id.skbar_blur_radius)
        val skbar_brush_size = findViewById<SeekBar>(R.id.skbar_brush_size)
        var radTextView = findViewById<TextView>(R.id.radiusText);
        var brushSizeTextView = findViewById<TextView>(R.id.brushText);

        var OnRadChangeListener: SeekBar.OnSeekBarChangeListener = object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                radTextView.setText("Radius: ${(skbar_blur_radius.getProgress().toFloat())}")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        }
        var OnBSizeChangeListener: SeekBar.OnSeekBarChangeListener = object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                brushSizeTextView.setText("BrushSize: ${(skbar_brush_size.getProgress().toFloat())}")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        }
        skbar_blur_radius.setOnSeekBarChangeListener(OnRadChangeListener)
        skbar_brush_size.setOnSeekBarChangeListener(OnBSizeChangeListener)

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            switchActivity(newUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
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
