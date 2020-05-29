package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_desktop.photo
import kotlinx.android.synthetic.main.activity_main.no
import kotlinx.android.synthetic.main.activity_main.yes
import kotlinx.android.synthetic.main.activity_retouching.*
import java.util.*


private var mCanvas: Canvas? = null
private var mPaint: Paint? = null
private val MemPixels: ArrayList<Pixel> = ArrayList<Pixel>()


class RetouchingActivity : AppCompatActivity() {
    private var imageUriUri: Uri? = null
    private var isChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        imageUriUri = imageUri
        photo.setImageURI(imageUri)
        val skbar_blur_radius = findViewById<SeekBar>(R.id.skbar_blur_radius)
        val skbar_brush_size = findViewById<SeekBar>(R.id.skbar_brush_size)
        var radTextView = findViewById<TextView>(R.id.radiusText);
        var brushSizeTextView = findViewById<TextView>(R.id.brushText);
        var b_p = (photo.getDrawable() as BitmapDrawable).bitmap
        var work_b_p = b_p.copy(b_p.config,true)
        val viewCoords = IntArray(2)
        photo.getLocationOnScreen(viewCoords)

        mCanvas = Canvas(work_b_p)
        mPaint = Paint()
        mPaint!!.setColor(Color.GREEN); // установим зеленый цвет
        mPaint!!.setStyle(Paint.Style.FILL);


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

        photo.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                val bitmapWidth: Float = work_b_p.getWidth().toFloat()
                val bitmapHeight: Float = work_b_p.getHeight().toFloat()

                val imageWidth: Float = photo.getWidth().toFloat()
                val imageHeight: Float = photo.getHeight().toFloat()

                val proportionateWidth = bitmapWidth / imageWidth
                val proportionateHeight = bitmapHeight / imageHeight

                val curX = (m.getX() * proportionateWidth)
                val curY = (m.getY() * proportionateHeight)

                var width:Int = work_b_p.width
                var height:Int = work_b_p.height
                var rad:Int = skbar_brush_size.getProgress() + 1
                mCanvas!!.drawCircle(curX, curY, rad.toFloat(), mPaint!!)

                val pixels = IntArray(height*width)
                work_b_p.getPixels(pixels,0,width,0,0,width,height)

                for (i in -rad..rad) {
                    for (j in -rad..rad) {
                       if (0 > curX + i || curX + i >= b_p.getWidth().toFloat()) {
                            continue
                        }
                        if (0 > curY + j || curY + j >= b_p.getHeight().toFloat()) {
                            continue
                        }
                       if (Math.sqrt(i * i + j * j.toDouble()) <= rad) {
                           MemPixels.add( Pixel(curX + i, curY + j, pixels[((curX + i)*width +  (curY + j)).toInt()]))
                       }
                    }
                }
                photo.setImageBitmap(work_b_p)
                return true
            }
        })
        //Retouching Applying
        doRetouchButton.setOnClickListener(object: View.OnClickListener
        {
            override fun onClick(v: View?) {
                isChanged = true
                // blurred bitmap
                var resBitmap = b_p.copy(b_p.config,true)
                // blurred bitmap
                var rad:Int = skbar_blur_radius.getProgress() + 1
                mCanvas!!.drawBitmap(work_b_p,work_b_p.width.toFloat(),work_b_p.height.toFloat(), mPaint)
                var blurred_b_p = boxBlur(resBitmap,rad)
                val width: Int? = blurred_b_p?.width
                val height:Int? = blurred_b_p?.height
                val blurredPixels = IntArray(height!! * width!!)
                blurred_b_p?.getPixels(blurredPixels,0,width,0,0,width,height)

                var resPixels = IntArray(resBitmap.width*resBitmap.height)
                resBitmap.getPixels(resPixels,0,width,0,0,width,height)
                for (i in MemPixels.indices) {
                    val e: Pixel = MemPixels[i]
                    if (blurred_b_p != null) {
                        resPixels[e.x.toInt()+ e.y.toInt()*width ] = blurredPixels[e.x.toInt()+ e.y.toInt()*width ]
                        //resBitmap.setPixel(e.x.toInt(),e.y.toInt(), blurred_b_p.getPixel(e.x.toInt(),e.y.toInt()))
                    }
                }
                resBitmap.setPixels(resPixels,0,width,0,0,width,height)
                photo.setImageBitmap(resBitmap)
                work_b_p = resBitmap
                mCanvas = Canvas(work_b_p)
            }
        })

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            switchActivity(newUri)
        }
        no.setOnClickListener {
            if(isChanged)
                quitDialog()
            else
                switchActivity(imageUri)
        }
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
