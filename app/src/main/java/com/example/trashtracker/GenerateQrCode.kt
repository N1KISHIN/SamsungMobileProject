package com.example.trashtracker

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trashtracker.databinding.ActivityCreateQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.Manifest
import android.util.Base64
import java.nio.charset.Charset

class GenerateQrCode : AppCompatActivity(){


    lateinit var binding: ActivityCreateQrCodeBinding
    private val REQUEST_PERMISSION_CODE = 123
    private lateinit var qrCodeBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val leftRadioGroup: RadioGroup = findViewById(R.id.leftRadioGroup)
        val rightRadioGroup: RadioGroup = findViewById(R.id.rightRadioGroup)

        val onCheckedChangeListener = object : RadioGroup.OnCheckedChangeListener {
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

        val rbMetal = binding.rbMetal
        val rbGlass = binding.rbGlass
        val rbPlastic = binding.rbPlastic
        val rbPaper = binding.rbPaper
        val rbBattery = binding.rbBattery
        val rbTech = binding.rbTech
        val rbDanger = binding.rbDanger
        var wasteType = ""
        val amountType = "Шт"
        val etName: EditText = binding.etName
        val etDescription: EditText = binding.etDescription
        val qrCodeHolder = binding.ivQr


        rbMetal.setOnClickListener { wasteType = "Металл" }
        rbGlass.setOnClickListener { wasteType = "Стекло" }
        rbPlastic.setOnClickListener { wasteType = "Пластик" }
        rbPaper.setOnClickListener { wasteType = "Бумага" }
        rbBattery.setOnClickListener { wasteType = "Батарейки" }
        rbTech.setOnClickListener { wasteType = "Техника" }
        rbDanger.setOnClickListener { wasteType = "Опасное" }



        val generateButton = binding.generateButton
        generateButton.setOnClickListener {
            val name = etName.text.toString()
            val description = etDescription.text.toString()
            val wasteTypeQr = wasteType // Здесь вместо "Металл" используйте выбранный тип отхода

            val data = "$name, $description, $wasteTypeQr"
            val dataBase64 = Base64.encodeToString(data.toByteArray(Charsets.UTF_8), Base64.DEFAULT)

            try {
                val bitMatrix: BitMatrix = MultiFormatWriter().encode(
                    dataBase64,
                    BarcodeFormat.QR_CODE,
                    300,
                    300
                )
                qrCodeBitmap = Bitmap.createBitmap(
                    bitMatrix.width,
                    bitMatrix.height,
                    Bitmap.Config.RGB_565
                )
                for (x in 0 until bitMatrix.width) {
                    for (y in 0 until bitMatrix.height) {
                        qrCodeBitmap.setPixel(
                            x,
                            y,
                            if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                        )
                    }
                }

                // Ваш код для отображения сгенерированного QR-кода (например, в ImageView)
                // val qrCodeImageView: ImageView = findViewById(R.id.qrCodeImageView)
                qrCodeHolder.setImageBitmap(qrCodeBitmap)

            } catch (e: WriterException) {
                e.printStackTrace()
            }
        }

        qrCodeHolder.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                saveQrCodeImage(qrCodeBitmap)
            } else {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSION_CODE)
            }
        }

    }

    private fun saveQrCodeImage(bitmap: Bitmap) {
        val displayName = "QRCodeImage.jpg"

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        imageUri?.let {
            try {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка при сохранении изображения", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQrCodeImage(qrCodeBitmap)
            } else {
                Toast.makeText(this, "Разрешение на запись не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    }

}