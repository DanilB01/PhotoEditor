package com.example.mobphotoedit

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_desktop.*

class DesktopActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private val itemAdapter by lazy {
        ItemAdapter { position: Int, item: Item ->
            var intent: Intent? = null
            when(position) {
                0 -> intent = Intent(DesktopActivity@this, ImageRotationActivity::class.java)
                1 -> intent = Intent(DesktopActivity@this, CorrectionActivity::class.java)
                2 -> intent = Intent(DesktopActivity@this, ImageScalingActivity::class.java)
                3 -> intent = Intent(DesktopActivity@this, SegmentationActivity::class.java)
                4 -> intent = Intent(DesktopActivity@this, LineDrawingActivity::class.java)
                5 -> intent = Intent(DesktopActivity@this, RetouchingActivity::class.java)
                6 -> intent = Intent(DesktopActivity@this, UnsharpMaskingActivity::class.java)
                7 -> intent = Intent(DesktopActivity@this, FilteringActivity::class.java)
                8 -> intent = Intent(DesktopActivity@this, CubeActivity::class.java)
            }
            intent?.putExtra("ImageUri", imageUri.toString())
            startActivity(intent)
            item_list.smoothScrollToPosition(position) //сглаживание анимации
        } }
    private val possibleItems = listOf( //список возможных иконок
        Item("Image Rotation", R.drawable.ic_crop_rotate),
        Item("Effects", R.drawable.ic_photo_filter),
        Item("Image Scaling", R.drawable.ic_image_scaling),
        Item("Segmentation", R.drawable.ic_segmentation),
        Item("Line Drawing", R.drawable.ic_line_drawing),
        Item("Retouching", R.drawable.ic_retoushing),
        Item("Unsharp Masking", R.drawable.ic_unsharp_masking),
        Item("Filtering", R.drawable.ic_filtering),
        Item("3D Cube", R.drawable.ic_3d_cube)
    )

    override fun onCreate(savedInstanceState: Bundle?) { //создание макета
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desktop)

        var string: String? = intent.getStringExtra("ImageUri")
        imageUri = Uri.parse(string)
        photo.setImageURI(imageUri)

        item_list.initialize(itemAdapter)
        item_list.setViewsToChangeColor(listOf(R.id.list_item_background, R.id.list_item_text))
        itemAdapter.setItems(getLargeListOfItems())


    }

    private fun getLargeListOfItems(): List<Item> {
        val items = mutableListOf<Item>()
        for(i in 0..8){
            items.add(possibleItems[i])
        }
        return items
    }
}

data class Item( //класс объекта
    val title: String,
    @DrawableRes val icon: Int
)