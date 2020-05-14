package com.example.mobphotoedit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ItemAdapter2(val itemClick: (position: Int,item: Item1) -> Unit) : RecyclerView.Adapter<ItemViewHolder2>() {

    private var items: List<Item1> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder2 =  // возвращает объект ViewHolder, который будет хранить данные по одному объекту Phone
        ItemViewHolder2(LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder2, position: Int) {  // выполняет привязку объекта ViewHolder к объекту Phone по определенной позиции
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            itemClick(position, items[position])
        }
    }

    override fun getItemCount() = items.size  // возвращает количество объектов в списке

    fun setItems(newItems: List<Item1>) {
        items = newItems
        notifyDataSetChanged() // уведомляет список об изменении данных для обновления списка на экране
    }
}

class ItemViewHolder2(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(item: Item1) {
        view.list_item_text.text = item.title
        view.list_item_icon.setImageResource(item.icon)
    }
}