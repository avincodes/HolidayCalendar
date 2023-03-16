package lk.nibm.holidaycalendar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.card.MaterialCardView
import lk.nibm.calendar.Adapter.HolidaysInMonthAdapter
import lk.nibm.holidaycalendar.Model.HolidaysModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var txtCountry: TextView
    private lateinit var txtHoliday: TextView
    private lateinit var btnLocalHolidays: Button
    private lateinit var btnWorldHolidays: Button
    private lateinit var recyclerViewHolidayInThisMonth: RecyclerView

    private lateinit var dialog: AlertDialog

    // Locations Permission
    private lateinit var fusedLocation: FusedLocationProviderClient
    var isPermissionGranted: Boolean = false
    private val LOCATION_REQUEST_CODE = 100

    private var countryId: String? = null

    private lateinit var holidaysInThisMonth: ArrayList<HolidaysModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeComponents()

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

        clickListeners()

    }

    private fun clickListeners()
    {
        btnLocalHolidays.setOnClickListener {
            val intent = Intent(this, LocalHolidaysActivity::class.java)
            intent.putExtra("countryId", countryId)
            startActivity(intent)
        }

        btnWorldHolidays.setOnClickListener {
            val intent = Intent(this, WorldHolidaysActivity::class.java)
            intent.putExtra("countryId", countryId)
            startActivity(intent)
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
        } else{
            isPermissionGranted = true
            if (isPermissionGranted){
                getCountryInAPI()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCountryInAPI() {
        dialog.show()
        if (isPermissionGranted){
            val locationResult = fusedLocation.lastLocation
            locationResult.addOnCompleteListener(this){location ->
                if (location.isSuccessful) {
                    val lastLocation = location.result
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(lastLocation!!.latitude, lastLocation.longitude, 1)
                    val country = addresses?.get(0)!!.countryName
                    txtCountry.text = country
                    if (country != null) {
                        getCountryId(country)
                    } else {
                        getHolidaysInThisMonth("LK")
                        dialog.dismiss()
                    }
                } else {
                    getHolidaysInThisMonth("LK")
                    dialog.dismiss()
                }
            }
        } else {
            txtCountry.text = "No Location Found !"
            getHolidaysInThisMonth("LK")
            dialog.dismiss()
        }
    }

    private fun getCountryId(country: String) {
        val url = "https://calendarific.com/api/v2/countries?api_key=" + resources.getString(R.string.API_KEY)
        val resultCountries = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayCountries = jsonObjectResponse.getJSONArray("countries")
                for (i in 0 until jsonArrayCountries.length()){
                    val jsonObjectCountry = jsonArrayCountries.getJSONObject(i)
                    if (jsonObjectCountry.getString("country_name") == country){
                        countryId = jsonObjectCountry.getString("iso-3166").toString()
                        getHolidaysInThisMonth(countryId!!)
                    }
                }
            }catch (e: Exception){
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(this, "" + error.message, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })
        Volley.newRequestQueue(this).add(resultCountries)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getHolidaysInThisMonth(countryId: String) {
        val url = "https://calendarific.com/api/v2/holidays?api_key=" + resources.getString(R.string.API_KEY) + "&country=" + countryId + "&year=" + SimpleDateFormat("yyyy",Locale.getDefault()).format(Date())

        val result = StringRequest(Request.Method.GET, url, Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayHolidays = jsonObjectResponse.getJSONArray("holidays")

                // Check holidays in this month
                val currentMonth = SimpleDateFormat("M",Locale.getDefault()).format(Date())
                val currentDay = SimpleDateFormat("d",Locale.getDefault()).format(Date())
                val longMonth = SimpleDateFormat("MMMM",Locale.getDefault()).format(Date())
                for (i in 0 until jsonArrayHolidays.length()){
                    val jsonObjectHolidayList = jsonArrayHolidays.getJSONObject(i)
                    val date = jsonObjectHolidayList.getJSONObject("date")
                    val dateTime = date.getJSONObject("datetime")
                    val month = dateTime.getString("month")
                    if (month == currentMonth){
                        val holidays = HolidaysModel()
                        holidays.holidayName = jsonObjectHolidayList.getString("name")
                        holidays.holidayDescription = jsonObjectHolidayList.getString("description")
                        holidays.holidayDate = dateTime.getString("day")
                        holidays.holidayMonth = dateTime.getString("month")
                        holidays.holidayYear = dateTime.getString("year")
                        holidaysInThisMonth.add(holidays)
                        if (currentDay == dateTime.getString("day")){
                            txtHoliday.text = jsonObjectHolidayList.getString("name")
                        }
                    }
                }

                // Pass holidays to adapter
                val adapter = HolidaysInMonthAdapter(this, holidaysInThisMonth)
                recyclerViewHolidayInThisMonth.setHasFixedSize(true)
                recyclerViewHolidayInThisMonth.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewHolidayInThisMonth.adapter = adapter
                adapter.notifyDataSetChanged()
                dialog.dismiss()

            }catch (e: Exception){
                Toast.makeText(this, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }, Response.ErrorListener {error ->
            Toast.makeText(this, "" + error.message.toString(), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })

        Volley.newRequestQueue(this).add(result)
    }

    private fun initializeComponents() {
        txtCountry = findViewById(R.id.txtCountry)
        txtHoliday = findViewById(R.id.txtHoliday)
        btnLocalHolidays = findViewById(R.id.btnLocalHolidays)
        btnWorldHolidays = findViewById(R.id.btnWorldHolidays)
        recyclerViewHolidayInThisMonth = findViewById(R.id.recyclerViewHolidayInThisMonth)

        holidaysInThisMonth = arrayListOf<HolidaysModel>()

        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setView(R.layout.progress_dialog)
        }.create().also {
            dialog = it
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        isPermissionGranted = false
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true
                    getCountryInAPI()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}