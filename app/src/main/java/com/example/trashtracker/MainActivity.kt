package com.example.trashtracker

import Companies
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.trashtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val goToStockBtn: Button = binding.stockButton
        goToStockBtn.setOnClickListener{
            val intent = Intent(this, StockActivity::class.java)
            startActivity(intent)
        }

        val addTrashButton: Button = binding.addTrashButton
        addTrashButton.setOnClickListener {
            val intent = Intent(this, AddTrash::class.java)
            startActivity(intent)
        }

        val recomndationsButton: Button = binding.recButton
        recomndationsButton.setOnClickListener {
            val intent = Intent(this, RecomendationsActivity::class.java)
            startActivity(intent)
        }

        val generateQrCodeButton: Button = binding.QrCodeButton
        generateQrCodeButton.setOnClickListener {
            val intent = Intent(this, GenerateQrCode::class.java)
            startActivity(intent)
        }

        val RecyclerCompaniesButton: Button = binding.RecyclerCompButton
        RecyclerCompaniesButton.setOnClickListener {
            val intent = Intent(this, Companies::class.java)
            startActivity(intent)
        }

    }
}