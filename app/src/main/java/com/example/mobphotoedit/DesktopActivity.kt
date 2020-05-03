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
        Item("Image Rotation", R.drawable.ic_check_black_24dp),
        Item("Effects & Color Correction", R.drawable.ic_check_black_24dp),
        Item("Image Scaling", R.drawable.ic_check_black_24dp),
        Item("Segmentation", R.drawable.ic_check_black_24dp),
        Item("Line Drawing", R.drawable.ic_check_black_24dp),
        Item("Retouching", R.drawable.ic_check_black_24dp),
        Item("Unsharp Masking", R.drawable.ic_check_black_24dp),
        Item("Bilinear & Trilinear Filtering", R.drawable.ic_check_black_24dp),
        Item("3D Cube", R.drawable.ic_check_black_24dp)
    )

    override fun onCreate(savedInstanceState: Bundle?) { //создание макета
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desktop)

        item_list.initialize(itemAdapter)
        item_list.setViewsToChangeColor(listOf(R.id.list_item_background, R.id.list_item_text))
        itemAdapter.setItems(getLargeListOfItems())

        var string: String? = intent.getStringExtra("ImageUri")
        var imageUri: Uri? = string?.toUri()


        photo.setImageURI(imageUri)
    }

    private fun getLargeListOfItems(): List<Item> {
        val items = mutableListOf<Item>()
        (0..40).map { items.add(possibleItems.random()) }
        return items
    }
}

data class Item( //класс объекта
    val title: String,
    @DrawableRes val icon: Int
)