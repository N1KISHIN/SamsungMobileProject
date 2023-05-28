package com.example.trashtracker

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder

class RecomendationsActivity : AppCompatActivity() {

    private lateinit var recom: List<Recomendations>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recomendations)

        val fileResult = application.assets.open("recomendations.json").bufferedReader().use { it.readText() }
        Log.d("RRR", fileResult)
        val gson = GsonBuilder().create()
        recom = gson.fromJson(fileResult, Array<Recomendations>::class.java).toList()
        Log.d("RRR", recom.get(0).recommendation)

        recyclerView = findViewById(R.id.rvRec)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MyAdapterRvRecomendations(recom)
        recyclerView.adapter = adapter

    }

}