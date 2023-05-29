import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trashtracker.CompanyData
import com.example.trashtracker.databinding.ActivityCompaniesBinding
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class RecyclerCompaniesInfo1 : AppCompatActivity() {
    private lateinit var binding: ActivityCompaniesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompaniesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textView: TextView = binding.textView
        val button: Button = binding.button

        button.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Разрешение на доступ к местоположению уже предоставлено
                getCurrentLocation(textView)
            } else {
                // Запросите разрешение на доступ к местоположению
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    private fun getCurrentLocation(textView: TextView) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLocation = Location("Current")
                    currentLocation.latitude = it.latitude // Широта текущего местоположения
                    currentLocation.longitude = it.longitude // Долгота текущего местоположения

                    val json = loadJsonFromAssets("RecyclerCompanies.json") // Загрузка JSON-файла с данными о компаниях
                    val gson = Gson()
                    val companies: List<CompanyData> =
                        gson.fromJson(json, Array<CompanyData>::class.java).toList()

                    var closestCompany: CompanyData? = null
                    var closestDistance: Float? = null

                    for (company in companies) {
                        val companyLocation = Location("Company")
                        companyLocation.latitude = company.lat
                        companyLocation.longitude = company.lon

                        val distance =
                            currentLocation.distanceTo(companyLocation) // Вычисление расстояния между текущим местоположением и компанией

                        if (closestDistance == null || distance < closestDistance) {
                            closestCompany = company
                            closestDistance = distance
                        }
                    }

                    if (closestCompany != null) {
                        textView.text = closestCompany.name // Выводим имя ближайшей компании в TextView
                    } else {
                        textView.text = "Нет данных о компаниях"
                    }
                }
            }
    }

    private fun loadJsonFromAssets(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val bufferedReader = BufferedReader(InputStreamReader(assets.open(fileName)))
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
