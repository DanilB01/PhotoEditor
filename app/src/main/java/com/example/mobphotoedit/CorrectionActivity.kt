package com.example.mobphotoedit

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_correction.*
import kotlinx.android.synthetic.main.activity_correction.item_list
import kotlinx.android.synthetic.main.activity_correction.photo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Item1( //класс объекта
    val title: String,
    @DrawableRes val icon: Int
)

private val possibleItems = listOf( //список возможных иконок
    Item1("negative", R.mipmap.ic_negativelast),
    Item1("brightness", R.mipmap.ic_brightlast),
    Item1("saturation", R.mipmap.ic_seturation1last),
    Item1("saturation2", R.mipmap.ic_setuarationlast2),
    Item1("grass", R.mipmap.ic_grasslast),
    Item1("movie", R.mipmap.ic_movie),
    Item1("ruby", R.mipmap.ic_rubulast),
    Item1("laguna", R.mipmap.ic_laguna)
)

class CorrectionActivity : AppCompatActivity() {
    private var imageUriUri: Uri? = null
    private var isChanged = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correction)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        var b_p =(photo.getDrawable() as BitmapDrawable).bitmap

        pBarEf.visibility = View.GONE
        photo.setImageURI(imageUri)
        b_p = checkBitmap(b_p,this)
        imageUriUri = imageUri

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            bitmapStore.addBitmap(imageView2Bitmap(photo))
            switchActivity(newUri)
        }
        no.setOnClickListener {
            if(isChanged)
                quitDialog()
            else
                switchActivity(imageUri)
        }
        val itemAdapter2 by lazy {
            ItemAdapter2 { position: Int, item: Item1 ->
                pBarEf.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Default).async {
                    var b_m: Bitmap? = null
                    when(position){
                        0 -> b_m = negativeFilter(b_p)
                        1 -> b_m = changeBrightnessFilter(b_p, 100F)
                        2 -> b_m = adjustSaturationFilter(b_p, 50F)
                        3 -> b_m = updateSaturationFilter(b_p,10F)
                        4 -> b_m = grassFilter(b_p)
                        5 -> b_m =movieFilter(b_p)
                        6 -> b_m = rubyFilter(b_p)
                        7 -> b_m = lagunaFilter(b_p)
                    }
                    launch(Dispatchers.Main) {
                        photo.setImageBitmap(b_m)
                        pBarEf.visibility = View.GONE }
                }
                isChanged = true
                item_list.smoothScrollToPosition(position) //сглаживание анимации
            }
        }

        item_list.initialize(itemAdapter2)
        item_list.setViewsToChangeColor(listOf(R.id.list_item_text))
        itemAdapter2.setItems(getLargeListOfItems())
    }
    private fun getLargeListOfItems(): List<Item1> {
        val items = mutableListOf<Item1>()
        for (i in 0..7) {
            items.add(possibleItems[i])
        }
        return items
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

//negative
fun negativeFilter(bitmold: Bitmap): Bitmap {
    var bitmnew = bitmold.copy(Bitmap.Config.ARGB_8888,true)
    val originalPixels = IntArray(bitmold.width*bitmold.height,{0})
    bitmold.getPixels(originalPixels,0,bitmold.width,0,0,bitmold.width,bitmold.height)
    for (y in 0 until bitmold.height) {
        for (x in 0 until bitmold.width) {
            val oldPixel = originalPixels[y*bitmold.width+x]

            val r = 255 - Color.red(oldPixel)
            val g = 255 - Color.green(oldPixel)
            val b = 255 - Color.blue(oldPixel)

            bitmnew.setPixel(x, y, Color.rgb(r, g ,b))
        }
    }
    return bitmnew
}

fun changeBrightnessFilter(bmp: Bitmap, brightness: Float): Bitmap {
    val cm = ColorMatrix(
        floatArrayOf(
            1f,
            0f,
            0f,
            0f,
            brightness,
            0f,
            1f,
            0f,
            0f,
            brightness,
            0f,
            0f,
            1f,
            0f,
            brightness,
            0f,
            0f,
            0f,
            1f,
            0f
        )
    )
    val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
    val canvas = Canvas(ret)
    val paint = Paint()
    paint.setColorFilter(ColorMatrixColorFilter(cm))
    canvas.drawBitmap(bmp,0F,0F,paint)
    return ret
}

fun adjustSaturationFilter(bmp: Bitmap, value: Float): Bitmap {
    val x = 1 +  3 * value / 100
    val lumR = 0.3086f
    val lumG = 0.6094f
    val lumB = 0.0820f
    val mat = floatArrayOf(
        lumR * (1 - x) + x,
        lumG * (1 - x),
        lumB * (1 - x),
        0f,
        0f,
        lumR * (1 - x),
        lumG * (1 - x) + x,
        lumB * (1 - x),
        0f,
        0f,
        lumR * (1 - x),
        lumG * (1 - x),
        lumB * (1 - x) + x,
        0f,
        0f,
        0f,
        0f,
        0f,
        1f,
        0f,
        0f,
        0f,
        0f,
        0f,
        1f
    )
    val cm = ColorMatrix(mat)
    val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config)
    val canvas = Canvas(ret)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(bmp, 0F, 0F, paint)
    return ret
}

fun updateSaturationFilter(src: Bitmap, settingSat: Float): Bitmap {
    val width = src.width
    val height = src.height
    val bitmapResult = Bitmap
        .createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvasResult = Canvas(bitmapResult)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(settingSat)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvasResult.drawBitmap(src, 0F, 0F, paint)
    return bitmapResult
}

//grass effect
fun grassFilter(sentBitmap: Bitmap): Bitmap {
    val bufBitmap =
        Bitmap.createBitmap(sentBitmap.width, sentBitmap.height, sentBitmap.config)
    for (i in 0 until sentBitmap.width) {
        for (j in 0 until sentBitmap.height) {
            val p = sentBitmap.getPixel(i, j)
            var r = Color.red(p)
            var g = Color.green(p)
            var b = Color.blue(p)
            r = (r * 0.9).toInt()
            b = (b * 0.9).toInt()
            g = (g * 1.2).toInt()
            if (g > 255) g = 255
            bufBitmap.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b))
        }
    }
    return bufBitmap
}

//as in movie
fun movieFilter(sentBitmap: Bitmap): Bitmap {
    val bufBitmap =
        Bitmap.createBitmap(sentBitmap.width, sentBitmap.height, sentBitmap.config)
    for (i in 0 until sentBitmap.width) {
        for (j in 0 until sentBitmap.height) {
            val p = sentBitmap.getPixel(i, j)
            val r = Color.red(p)
            var g = Color.green(p)
            var b = Color.blue(p)
            b = (b * 0.8).toInt()
            g = (g * 0.9).toInt()
            bufBitmap.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b))
        }
    }
    return bufBitmap
}

//underwater
fun lagunaFilter(sentBitmap: Bitmap): Bitmap {
    val bufBitmap =
        Bitmap.createBitmap(sentBitmap.width, sentBitmap.height, sentBitmap.config)
    for (i in 0 until sentBitmap.width) {
        for (j in 0 until sentBitmap.height) {
            val p = sentBitmap.getPixel(i, j)
            var r = Color.red(p)
            val g = Color.green(p)
            var b = Color.blue(p)
            r = (r * 0.9).toInt()
            b = (b * 1.2).toInt()
            if (b > 255) b = 255
            bufBitmap.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b))
        }
    }
   return bufBitmap
}

//just ruby
fun rubyFilter(sentBitmap: Bitmap): Bitmap {
    val bufBitmap =
        Bitmap.createBitmap(sentBitmap.width, sentBitmap.height, sentBitmap.config)
    for (i in 0 until sentBitmap.width) {
        for (j in 0 until sentBitmap.height) {
            val p = sentBitmap.getPixel(i, j)
            var r = Color.red(p)
            var g = Color.green(p)
            val b = Color.blue(p)
            g = (g * 0.8).toInt()
            r = (r * 1.2).toInt()
            if (r > 255) r = 255
            bufBitmap.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b))
        }
    }
    return bufBitmap
}