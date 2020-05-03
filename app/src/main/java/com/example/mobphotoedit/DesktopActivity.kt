package com.example.mobphotoedit

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

    private val itemAdapter by lazy {
        ItemAdapter { position: Int, item: Item ->
            Toast.makeText(this@DesktopActivity, "Pos ${position}", Toast.LENGTH_LONG).show() //показывает позицию и выводит юзеру
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
        var imageUri: Uri? = Uri.parse(string)
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