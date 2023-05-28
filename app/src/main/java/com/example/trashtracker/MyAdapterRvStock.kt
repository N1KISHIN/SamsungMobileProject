package com.example.trashtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapterRvStock(var list: List<Waste>) : RecyclerView.Adapter<MyAdapterRvStock.MyViewHolder>() {
    class MyViewHolder(item: View): RecyclerView.ViewHolder(item){
        val wasteName: TextView = item.findViewById(R.id.tvName)
        val wasteAmount: TextView = item.findViewById(R.id.tvAmount)
        val wasteAmountType: TextView = item.findViewById(R.id.tvAmountType)
        val imgPhoto: ImageView = item.findViewById(R.id.imgPhoto)
        val time: TextView = item.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItem = LayoutInflater.from(parent.context).inflate(R.layout.rv_stock_item, parent, false)
        return MyViewHolder(myItem)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.wasteName.text = list[position].name
        holder.wasteAmount.text = list[position].amount
        holder.wasteAmountType.text = list[position].amountType
        holder.time.text = list[position].time
        holder.imgPhoto.setImageResource(when (list[position].type){
            "Металл" -> R.drawable.metal
            "Стекло" -> R.drawable.glass
            "Пластик" -> R.drawable.plastic
            "Бумага" -> R.drawable.paper
            "Батарейки" -> R.drawable.battery
            "Техника" -> R.drawable.tech
            "Ткань" -> R.drawable.cloth
            "Опасное" -> R.drawable.danger
            else -> R.drawable.baseline_5k_24
        })

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(newList: List<Waste>) {
        list = newList
        notifyDataSetChanged()
    }

}