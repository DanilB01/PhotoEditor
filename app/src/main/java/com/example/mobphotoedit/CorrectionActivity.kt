package com.example.mobphotoedit

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_correction.*


class CorrectionActivity : AppCompatActivity() {

    private val itemAdapter2 by lazy {
        ItemAdapter2 { position: Int, item: Item1 ->

            when (position) {
                0 -> filter1(photo)
                1 -> changeBrightness(imageView2Bitmap(photo), 100F, photo)
                2 -> adjustSaturation(imageView2Bitmap(photo), 50F, photo)
                3 -> updateSaturation(imageView2Bitmap(photo),10F,photo)
                4 -> grassFilter(imageView2Bitmap(photo),photo)
                5 -> movieFilter(imageView2Bitmap(photo), photo)
                6 -> rubyFilter(imageView2Bitmap(photo),photo)
                7 -> lagunaFilter(imageView2Bitmap(photo),photo)
            }

            item_list.smoothScrollToPosition(position) //сглаживание анимации
        }
    }

    private val possibleItems = listOf( //список возможных иконок
        Item1("negative", R.drawable.ic_photo_filter),
        Item1("brightness", R.drawable.ic_photo_filter),
        Item1("saturation", R.drawable.ic_photo_filter),
        Item1("saturation2", R.drawable.ic_photo_filter),
        Item1("grass", R.drawable.ic_photo_filter),
        Item1("movie", R.drawable.ic_photo_filter),
        Item1("ruby", R.drawable.ic_photo_filter),
        Item1("laguna", R.drawable.ic_photo_filter)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correction)

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)

        yes.setOnClickListener {
            var newUri = saveImageToInternalStorage(photo,this)
            switchActivity(newUri)
        }
        no.setOnClickListener {
            switchActivity(imageUri)
        }
        val dontforgetfirstbit = imageView2Bitmap(photo)

        item_list.initialize(itemAdapter2)
        item_list.setViewsToChangeColor(listOf(R.id.list_item_text))
        itemAdapter2.setItems(getLargeListOfItems())
        // filter2(photo)
        // val bitmap2 = imageView2Bitmap(photo)
        // val bitmap3= changeBrightness(bitmap2,100F)
        // photo.setImageBitmap(bitmap3)
        //adjustSaturation(bitmap2,300F)
        // val bitmap4 = updateSaturation(bitmap2, 20F)
        //  photo.setImageBitmap(bitmap4)


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
}
//negative
fun filter1(photo: ImageView) {
    var bitmold = imageView2Bitmap(photo)
    var bitmnew = bitmold.copy(Bitmap.Config.ARGB_8888,true)
    for (y in 0 until bitmold.height) {
        for (x in 0 until bitmold.width) {
            val oldPixel = bitmold.getPixel(x, y)

            val r = 255 - Color.red(oldPixel)
            val g = 255 - Color.green(oldPixel)
            val b = 255 - Color.blue(oldPixel)

            bitmnew.setPixel(x, y, Color.rgb(r, g ,b))
        }
    }

    photo.setImageBitmap(bitmnew)
}
//yellow filter
fun filter2(photo: ImageView) {
    var bitmold = imageView2Bitmap(photo)
    var bitmnew = bitmold.copy(Bitmap.Config.ARGB_8888,true)
    for (y in 0 until bitmold.height  ) {
        for (x in 0 until bitmold.width ) {
            val oldPixel = bitmold.getPixel(x, y)

            val r = Color.red(oldPixel)  + Color.LTGRAY
            val g =  Color.green(oldPixel)+ Color.LTGRAY
            val b = Color.blue(oldPixel) + Color.LTGRAY

            bitmnew.setPixel(x, y, Color.rgb(r, g ,b))
        }
    }

    photo.setImageBitmap(bitmnew)
}


data class Item1( //класс объекта
    val title: String,
    @DrawableRes val icon: Int
)

private fun imageView2Bitmap(view: ImageView):Bitmap{
    var bitmap: Bitmap
    bitmap =(view.getDrawable() as BitmapDrawable).bitmap
    return bitmap
}

fun changeBrightness(bmp: Bitmap, brightness: Float, photo: ImageView) {
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
    // canvas.drawBitmap(bmp, 0, 0, paint)


    canvas.drawBitmap(bmp,0F,0F,paint)
    photo.setImageBitmap(ret)
}

fun adjustSaturation(bmp: Bitmap, value: Float, photo: ImageView) {


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
    photo.setImageBitmap(ret)
}

fun updateSaturation(src: Bitmap, settingSat: Float, photo: ImageView) {
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
    photo.setImageBitmap(bitmapResult)
}

fun grassFilter(sentBitmap: Bitmap, photo: ImageView) {
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
    photo.setImageBitmap(bufBitmap)
}

fun movieFilter(sentBitmap: Bitmap, photo: ImageView) {
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
    photo.setImageBitmap(bufBitmap)
}

fun lagunaFilter(sentBitmap: Bitmap, photo:ImageView) {
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
    photo.setImageBitmap(bufBitmap)
}

fun rubyFilter(sentBitmap: Bitmap,photo: ImageView) {
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
    photo.setImageBitmap(bufBitmap)
}