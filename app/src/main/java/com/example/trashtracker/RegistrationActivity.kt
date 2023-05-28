package com.example.trashtracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.trashtracker.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val singUpButton: Button = findViewById(R.id.singUpButton1)
        singUpButton.setOnClickListener{
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

//            if (email == "user" && password == "1234") {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, "Неправильно введен пароль или логин", Toast.LENGTH_SHORT).show()
//            }
        }

    }
}