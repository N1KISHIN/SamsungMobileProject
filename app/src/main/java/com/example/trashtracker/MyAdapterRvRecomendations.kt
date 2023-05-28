package com.example.trashtracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapterRvRecomendations(var list: List<Recomendations>) : RecyclerView.Adapter<MyAdapterRvRecomendations.MyViewHolder>(){
    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item){
        val tvname: TextView = item.findViewById(R.id.tvWasteName)
        val tvrecommendation: TextView = item.findViewById(R.id.tvWasteRecomendation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val myItem = LayoutInflater.from(parent.context).inflate(R.layout.rv_recomendations_item, parent, false)
        return MyViewHolder(myItem)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.tvname.text = list[position].name
       holder.tvrecommendation.text = list[position].recommendation

    }

    override fun getItemCount(): Int {
        return list.size
    }
}