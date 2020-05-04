package com.example.mobphotoedit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ItemAdapter(val itemClick: (position: Int,item: Item) -> Unit) : RecyclerView.Adapter<ItemViewHolder>() {

    private var items: List<Item> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =  // возвращает объект ViewHolder, который будет хранить данные по одному объекту Phone
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {  // выполняет привязку объекта ViewHolder к объекту Phone по определенной позиции
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            itemClick(position, items[position])
        }
    }

    override fun getItemCount() = items.size  // возвращает количество объектов в списке

    fun setItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged() // уведомляет список об изменении данных для обновления списка на экране
    }
}

class ItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item) {
        view.list_item_text.text = item.title
        view.list_item_icon.setImageResource(item.icon)
    }
}