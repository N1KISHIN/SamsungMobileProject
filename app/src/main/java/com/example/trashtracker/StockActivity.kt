package com.example.trashtracker

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trashtracker.databinding.ActivityStockBinding
import com.google.firebase.database.*
import com.google.gson.GsonBuilder
import java.io.File

class StockActivity : AppCompatActivity() {
    lateinit var binding: ActivityStockBinding
    private lateinit var waste: List<Waste>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapterRvStock
    private lateinit var dbRef : DatabaseReference
    private lateinit var list: ArrayList<Waste>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStockBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapterRvStock(emptyList())
        recyclerView.adapter = adapter
        list = ArrayList()



        getWasteData()
//        loadWasteData()
//        binding.deleteAllBtn.setOnClickListener { deleteAllItems() }
    }

//    private fun deleteAllItems() {
//        val file = File(filesDir, "stock.json")
//        if (file.exists()) {
//            file.delete()
//            adapter.updateData(emptyList())
//        }
//    }

//    private fun loadWasteData() {
//        val file = File(filesDir, "stock.json")
//        val gson = GsonBuilder().create()
//
//        if (file.exists()) {
//            val fileResult = file.bufferedReader().use { it.readText() }
//            waste = gson.fromJson(fileResult, Array<Waste>::class.java).toList()
//            adapter.updateData(waste)
//        }
//    }
//
//
//
//    override fun onResume() {
//        super.onResume()
//        loadWasteData()
//    }

    private fun getWasteData(){
        dbRef = FirebaseDatabase.getInstance().getReference("Waste")
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                if (snapshot.exists()){
                    for (snap in snapshot.children){
                        val wasteData = snap.getValue(Waste::class.java)
                        if (wasteData != null) {
                            list.add(wasteData)
                        }
                    }
                    val adapter = MyAdapterRvStock(list)
                    recyclerView.adapter = adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

}



