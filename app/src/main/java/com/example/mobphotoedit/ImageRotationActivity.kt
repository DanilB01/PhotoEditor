package com.example.mobphotoedit

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_desktop.photo
import kotlinx.android.synthetic.main.activity_image_rotation.*
import kotlinx.android.synthetic.main.activity_main.no
import kotlinx.android.synthetic.main.activity_main.yes
import java.lang.Math.*


fun  getRotated(src:Bitmap, degrees:Double):Bitmap
{
    var rads: Double = (degrees*Math.PI) / 180.0
    val width:Int = src.width
    val height:Int = src.height

    var cosa = cos(rads)
    var sina = sin(rads)

    var x1 = (-height*sina).toInt()
    var y1 = (height*cosa).toInt()
    var x2 = (width*cosa - height*sina).toInt()
    var y2 = (height*cosa + width*sina).toInt()
    var x3 = (width*cosa).toInt()
    var y3 = (width*sina).toInt()

    var minX = min(0, min(x1, min(x2, x3)))
    var minY = min(0, min(y1, min(y2, y3)))
    var maxX = max(x1, max(x2, x3))
    var maxY = max(y1, max(y2, y3))


    var curWidth:Int = maxX - minX
    var curHeight:Int = maxY - minY
    var rotatedBitmap = Bitmap.createBitmap(curWidth,  curHeight, src.config)

    val pixels = IntArray(height*width)
    src.getPixels(pixels,0,width,0,0,width,height)
    val newPixels = IntArray(curHeight*curWidth)
    for (y in 0 until curHeight)
    {
        for(x in 0 until curWidth)
        {
            var src_x:Int = ((x+minX)*cosa + (y+minY)*sina ).toInt()
            var src_y:Int = ((y+minY)*cosa - (x+minX)*sina ).toInt()
            var ind = src_y * width + src_x
            if (src_x >= 0 && src_x < width && src_y >= 0 && src_y < height) {
                newPixels[y*curWidth + x] = pixels[ind]
            }
            else {
                newPixels[y*curWidth + x] = 0
            }
        }
    }
    rotatedBitmap.setPixels(newPixels,0,curWidth,0,0,curWidth,curHeight)
    return rotatedBitmap
}

/*fun setNewBitmap(currentImage: ImageView, b_p: Bitmap)
{
    var bmp = getDecodedBitmap(b_p)
    currentImage.setImageBitmap(bmp)
}*/

fun rotateImage(skbar: SeekBar, currentImage: ImageView, b_p: Bitmap,isRotatedRight:Boolean=false) {
    var degrees: Double = (skbar.getProgress() - 180).toDouble()
    var workBP:Bitmap
    if (isRotatedRight) {
        workBP = (currentImage.getDrawable() as BitmapDrawable).bitmap
    }
    else
    {
        workBP = b_p.copy(b_p.config, true)
    }
    if (abs(degrees) > 90)
    {
        if(degrees > 0) {
            degrees -= 90.0
            workBP = getRotated(workBP,90.0)
        }
        else {
            degrees += 90.0
            workBP = getRotated(workBP,-90.0)
        }
    }
    var newBitmap: Bitmap = getRotated(workBP, degrees)
    currentImage.setImageBitmap(newBitmap)
}

fun rotateRight(currentImage: ImageView) {
    var degrees: Double = 90.0
    var workBP = (currentImage.getDrawable() as BitmapDrawable).bitmap
    var newBitmap: Bitmap = getRotated(workBP, degrees)
    currentImage.setImageBitmap(newBitmap)
}

class ImageRotationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_rotation)

        var mTextView = findViewById<TextView>(R.id.Scale_Text);
        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        var angle = 0.0
        var isRotatedRight = false

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
                rotateImage(skbar, photo, b_p,isRotatedRight)
            }
        }

        skbar.setOnSeekBarChangeListener(OnRotateChangeListener)

        Rotate_right.setOnClickListener{
            isRotatedRight=true
            rotateRight(photo)
            skbar.progress=180
        }
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

