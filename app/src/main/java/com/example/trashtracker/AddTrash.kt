package com.example.trashtracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.trashtracker.databinding.ActivityAddTrashBinding
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type
import com.google.gson.GsonBuilder
import java.io.FileReader
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RadioGroup.OnCheckedChangeListener
import android.widget.TextView
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlin.reflect.typeOf
import android.util.Base64
import java.nio.charset.Charset


class AddTrash : AppCompatActivity() {
    lateinit var binding: ActivityAddTrashBinding
    private lateinit var adapter: MyAdapterRvStock
    private lateinit var dbRef : DatabaseReference
    private lateinit var dbRefAss : DatabaseReference
    lateinit var scanButton: Button
    private var selectedWasteType: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTrashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = MyAdapterRvStock((emptyList()))
        val rb1 = binding.radioButton1
        val rb2 = binding.radioButton2
        var amountType: String = "Шт"
        val enterBtn = binding.enterBtn

        var wasteType = "Опасное"

        val rbMetal = binding.rbMetal
        val rbGlass = binding.rbGlass
        val rbPlastic = binding.rbPlastic
        val rbPaper = binding.rbPaper
        val rbBattery = binding.rbBattery
        val rbTech = binding.rbTech
        val rbDanger = binding.rbDanger
        val rbCloth = binding.rbCloth


        rbMetal.setOnClickListener { wasteType = "Металл" }
        rbGlass.setOnClickListener { wasteType = "Стекло" }
        rbPlastic.setOnClickListener { wasteType = "Пластик" }
        rbPaper.setOnClickListener { wasteType = "Бумага" }
        rbBattery.setOnClickListener { wasteType = "Батарейки" }
        rbTech.setOnClickListener { wasteType = "Техника" }
        rbDanger.setOnClickListener { wasteType = "Опасное" }
        rbCloth.setOnClickListener { wasteType = "Ткань" }

        dbRef = FirebaseDatabase.getInstance().getReference("Waste")
        dbRefAss = FirebaseDatabase.getInstance().getReference("Associations")

        rb1.setOnClickListener { amountType = "Шт"
            rb2.isChecked = false}
        rb2.setOnClickListener { amountType = "Кг"
            rb1.isChecked = false}

        val leftRadioGroup: RadioGroup = findViewById(R.id.leftRadioGroup)
        val rightRadioGroup: RadioGroup = findViewById(R.id.rightRadioGroup)

        val onCheckedChangeListener = object : OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId != -1) { // Проверяем, что была выбрана кнопка
                    if (group == leftRadioGroup) {
                        rightRadioGroup.setOnCheckedChangeListener(null) // Убираем обработчик события у другого RadioGroup
                        rightRadioGroup.clearCheck() // Снимаем выбор с кнопок в другом RadioGroup
                        rightRadioGroup.setOnCheckedChangeListener(this) // Возвращаем обработчик события обратно
                    } else if (group == rightRadioGroup) {
                        leftRadioGroup.setOnCheckedChangeListener(null) // Убираем обработчик события у другого RadioGroup
                        leftRadioGroup.clearCheck() // Снимаем выбор с кнопок в другом RadioGroup
                        leftRadioGroup.setOnCheckedChangeListener(this) // Возвращаем обработчик события обратно
                    }
                }
            }
        }

        leftRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener)
        rightRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener)
        val radioGroup = binding.radioGroup
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbMetal -> wasteType = "Металл"
                R.id.rbGlass -> wasteType = "Стекло"
                R.id.rbPlastic -> wasteType = "Пластик"
                R.id.rbPaper -> wasteType = "Бумага"
                R.id.rbBattery -> wasteType = "Батарейки"
                R.id.rbTech -> wasteType = "Техника"
                R.id.rbDanger -> wasteType = "Опасное"
                R.id.rbCloth -> wasteType = "Ткань"
                else -> wasteType = "Опасное" // Добавьте соответствующее значение по умолчанию
            }
        }


        enterBtn.setOnClickListener {
            val nameEt: String = binding.TrashNameEditText.text.toString()
            val amountEt: String = binding.TrashAmountEditTextText.text.toString()

            if (nameEt.isNotEmpty() && amountEt.isNotEmpty()){ // JSON
                var tvDescription = binding.TVdescription
                tvDescription.visibility = View.GONE
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                val type: String = wasteType
                val resultToAdd = Waste(nameEt, amountEt, amountType,dateFormat.format(currentDate).toString(), type)
                writeData(amountType, type)
                Toast.makeText(this,"Добавленно на склад! :)", Toast.LENGTH_SHORT).show()
                binding.TrashNameEditText.setText("")
                binding.TrashAmountEditTextText.setText("")
            }
            else{
                Toast.makeText(this,"некорректное значение полей", Toast.LENGTH_SHORT).show()
            }
        }

        scanButton = binding.scanButton
        scanButton.setOnClickListener {
            startQrCodeScanner()
        }

    }

    private fun startQrCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setOrientationLocked(true) // Разрешить поворот экрана при сканировании
        integrator.setBeepEnabled(false) // Отключить звуковой сигнал при сканировании
        integrator.setPrompt("Наведите камеру на QR-код") // Текстовая подсказка для сканирования
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents != null) {
            val scannedData = result.contents
            val decodedData = String(Base64.decode(scannedData.toByteArray(), Base64.DEFAULT), Charsets.UTF_8)
            val fields = decodedData.split(", ")

            if (fields.size >= 3) {
                val etName: EditText = binding.TrashNameEditText
                val tvDescription: TextView = binding.TVdescription
                var wasteType = fields[2]
                var etAmount: EditText = binding.TrashAmountEditTextText


                Log.d("Result1", fields[0].toString())
                Log.d("Result1", fields[1].toString())
                Log.d("Result1", fields[2].toString())

                etName.setText(fields[0])
                tvDescription.visibility = View.VISIBLE
                tvDescription.text = fields[1]
                etAmount.setText("1")



                val radioGroup: RadioGroup = binding.radioGroup
                radioGroup.check(R.id.radioButton1)
                when (wasteType){
                    "Металл" -> {
                        radioGroup.check(R.id.rbMetal)}
                    "Стекло" -> {
                        radioGroup.check(R.id.rbGlass)}
                    "Пластик" -> {
                        radioGroup.check(R.id.rbPlastic)}
                    "Бумага" -> {
                        radioGroup.check(R.id.rbPaper)}
                    "Батарейки" -> {
                        radioGroup.check(R.id.rbBattery)}
                    "Техника" -> {
                        radioGroup.check(R.id.rbTech)}
                    "Опасное" -> {
                        radioGroup.check(R.id.rbDanger)}
                    "Ткань" -> {
                        radioGroup.check(R.id.rbCloth)}
                }
                adapter.notifyDataSetChanged()

//                "Металл" -> { binding.rbMetal.isChecked = true
//                    radioGroup.check(R.id.rbMetal)}
//                "Стекло" -> {binding.rbGlass.isChecked = true
//                    radioGroup.check(R.id.rbGlass)}
//                "Пластик" -> {binding.rbPlastic.isChecked = true
//                    radioGroup.check(R.id.rbPlastic)}
//                "Бумага" -> {binding.rbPaper.isChecked = true
//                    radioGroup.check(R.id.rbPaper)}
//                "Батарейки" -> {binding.rbBattery.isChecked = true
//                    radioGroup.check(R.id.rbBattery)}
//                "Техника" -> {binding.rbTech.isChecked = true
//                    radioGroup.check(R.id.rbTech)}
//                "Опасное" -> {binding.rbDanger.isChecked = true
//                    radioGroup.check(R.id.rbDanger)}
//                "Ткань" -> {binding.rbCloth.isChecked = true
//                    radioGroup.check(R.id.rbCloth)}


            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }



    private fun writeData(amountType: String, type: String) {
        val nameEt: String = binding.TrashNameEditText.text.toString()
        val amountEt: String = binding.TrashAmountEditTextText.text.toString()
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val type: String = type

        if (nameEt.isNotEmpty()) {
            binding.TrashNameEditText.error = "Введите нименование"
        }
        if (amountEt.isNotEmpty()) {
            binding.TrashAmountEditTextText.error = "Введите количество"
        }
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (childSnapshot in snapshot.children) {
                        val wasteData = childSnapshot.getValue(Waste::class.java)
                        if (wasteData != null && wasteData.name == nameEt) {
                            val newAmount = (wasteData.amount.toDoubleOrNull() ?: 0.0) + (amountEt.toDoubleOrNull() ?: 0.0)
                            childSnapshot.ref.child("amount").setValue(newAmount.toString())
                            return
                        }
                    }
                }
                val nameID = dbRef.push().key!!
                val waste = Waste(nameEt, amountEt, amountType, dateFormat.format(currentDate).toString(), type.toString())

                dbRef.child(nameID).setValue(waste)
                    .addOnCompleteListener {
                        Toast.makeText(this@AddTrash, "Добавлено на склад! :)", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@AddTrash, "Ошибка записи в БД ;(", Toast.LENGTH_SHORT).show()
                    }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddTrash, "Ошибка чтения из БД ;(", Toast.LENGTH_SHORT).show()
            }
        })
    }
}


