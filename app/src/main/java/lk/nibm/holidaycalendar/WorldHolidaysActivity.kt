package lk.nibm.holidaycalendar

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import lk.nibm.calendar.Adapter.HolidayAdapter
import lk.nibm.holidaycalendar.Model.HolidaysModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class WorldHolidaysActivity : AppCompatActivity() {

    private lateinit var cardViewBack: MaterialCardView
    private lateinit var cardViewFilter: MaterialCardView
    private lateinit var recyclerViewWorldHoliday: RecyclerView

    private lateinit var dialog: AlertDialog

    private lateinit var holidaysList: ArrayList<HolidaysModel>

    var countryId: String = ""
    var countryIdSelected: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_holidays)

        countryId = intent.getStringExtra("countryId").toString()

        initializeComponents()

        getHolidays()

        clickListeners()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getHolidays(year: String, month: String, country: String) {
        dialog.show()
        holidaysList.clear()

        val url = "https://calendarific.com/api/v2/holidays?api_key=" + resources.getString(R.string.API_KEY) + "&country=" + country + "&year=" + year

        val result = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayHolidays = jsonObjectResponse.getJSONArray("holidays")
                for (i in 0 until jsonArrayHolidays.length()){
                    val jsonObjectHolidayList = jsonArrayHolidays.getJSONObject(i)
                    val date = jsonObjectHolidayList.getJSONObject("date")
                    val dateTime = date.getJSONObject("datetime")
                    val holidays = HolidaysModel()
                    if (dateTime.getString("month") == month){
                        holidays.holidayName = jsonObjectHolidayList.getString("name")
                        holidays.holidayDescription = jsonObjectHolidayList.getString("description")
                        holidays.holidayDate = dateTime.getString("day")
                        holidays.holidayMonth = dateTime.getString("month")
                        holidays.holidayYear = dateTime.getString("year")
                        holidaysList.add(holidays)
                    } else if (month == "0"){
                        holidays.holidayName = jsonObjectHolidayList.getString("name")
                        holidays.holidayDescription = jsonObjectHolidayList.getString("description")
                        holidays.holidayDate = dateTime.getString("day")
                        holidays.holidayMonth = dateTime.getString("month")
                        holidays.holidayYear = dateTime.getString("year")
                        holidaysList.add(holidays)
                    }
                }
                // Pass holidays to adapter
                val adapter = HolidayAdapter(this, holidaysList)
                recyclerViewWorldHoliday.setHasFixedSize(true)
                recyclerViewWorldHoliday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewWorldHoliday.adapter = adapter
                adapter.notifyDataSetChanged()
                dialog.dismiss()

            }catch (e: Exception){
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }, Response.ErrorListener { error ->
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })

        Volley.newRequestQueue(this).add(result)

    }

    private fun clickListeners() {
        cardViewBack.setOnClickListener {
            onBackPressed()
        }

        cardViewFilter.setOnClickListener {
            openFilterDialogBox()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getHolidays() {
        dialog.show()
        holidaysList.clear()
        val url = "https://calendarific.com/api/v2/holidays?api_key=" + resources.getString(R.string.API_KEY) + "&country=" + countryId + "&year=" + SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

        val result = StringRequest(Request.Method.GET, url, Response.Listener { response ->

            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayHolidays = jsonObjectResponse.getJSONArray("holidays")

                for (i in 0 until jsonArrayHolidays.length()){
                    val jsonObjectHolidayList = jsonArrayHolidays.getJSONObject(i)
                    val date = jsonObjectHolidayList.getJSONObject("date")
                    val dateTime = date.getJSONObject("datetime")
                    val holidays = HolidaysModel()
                    holidays.holidayName = jsonObjectHolidayList.getString("name")
                    holidays.holidayDescription = jsonObjectHolidayList.getString("description")
                    holidays.holidayDate = dateTime.getString("day")
                    holidays.holidayMonth = dateTime.getString("month")
                    holidays.holidayYear = dateTime.getString("year")
                    holidaysList.add(holidays)
                }

                // Pass holidays to adapter
                val adapter = HolidayAdapter(this, holidaysList)
                recyclerViewWorldHoliday.setHasFixedSize(true)
                recyclerViewWorldHoliday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewWorldHoliday.adapter = adapter
                adapter.notifyDataSetChanged()
                dialog.dismiss()

            }catch (e: Exception){
                Toast.makeText(this, "" + e.message.toString(), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }, Response.ErrorListener { error ->
            Toast.makeText(this, "" + error.message.toString(), Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })

        Volley.newRequestQueue(this).add(result)
    }

    private fun openFilterDialogBox() {
        val builder = MaterialAlertDialogBuilder(this)
        val view = layoutInflater.inflate(R.layout.filter_with_country_dialog_box, null)
        builder.setCancelable(false)

        val spinnerYear = view.findViewById<Spinner>(R.id.spinnerYear)
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)
        val spinnerCountry = view.findViewById<Spinner>(R.id.spinnerCountry)

        // set passed 10 years to spinner
        val years = ArrayList<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 0..10){
            years.add((currentYear - i).toString())
        }
        val adapterYear = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)
        spinnerYear?.adapter = adapterYear

        // set months to spinner
        val months = ArrayList<String>()
        months.add("All Months")
        months.add("January")
        months.add("February")
        months.add("March")
        months.add("April")
        months.add("May")
        months.add("June")
        months.add("July")
        months.add("August")
        months.add("September")
        months.add("October")
        months.add("November")
        months.add("December")
        val adapterMonth = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, months)
        spinnerMonth?.adapter = adapterMonth

        // set countries to spinner from API
        val countries = ArrayList<String>()
        countries.add("Select Country")
        dialog.show()
        val url = "https://calendarific.com/api/v2/countries?api_key=" + resources.getString(R.string.API_KEY)
        val resultCountries = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayCountries = jsonObjectResponse.getJSONArray("countries")
                for (i in 0 until jsonArrayCountries.length()){
                    val jsonObjectCountry = jsonArrayCountries.getJSONObject(i)
                    countries.add(jsonObjectCountry.getString("country_name"))
                }
                val adapterCountry = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, countries)
                spinnerCountry?.adapter = adapterCountry
                dialog.dismiss()
            }catch (e: Exception){
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }, Response.ErrorListener { error ->
            Toast.makeText(this, "" + error.message, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        })
        Volley.newRequestQueue(this).add(resultCountries)

        spinnerCountry?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (spinnerCountry?.selectedItem.toString() == "Select Country" && spinnerCountry?.selectedItemId == 0L){

                } else {
                    getCountryId(spinnerCountry?.selectedItem.toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@WorldHolidaysActivity, "Please select a country", Toast.LENGTH_SHORT).show()
            }

        }

        builder.setPositiveButton("Filter") { dialog, which ->
            val year = spinnerYear.selectedItem.toString()
            val month = spinnerMonth.selectedItemId.toString()
            getHolidays(year, month, countryIdSelected)
        }

        builder.setNegativeButton("Clear") { dialog, which ->
            spinnerYear.setSelection(0)
            spinnerMonth.setSelection(0)
            getHolidays()
        }

        builder.setNeutralButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        builder.setView(view)
        val dialogFilter = builder.create()
        dialogFilter.show()
    }

    private fun getCountryId(country: String) {
        dialog.show()
        val url = "https://calendarific.com/api/v2/countries?api_key=" + resources.getString(R.string.API_KEY)
        val resultCountries = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val jsonObjectResponse = jsonObject.getJSONObject("response")
                val jsonArrayCountries = jsonObjectResponse.getJSONArray("countries")
                for (i in 0 until jsonArrayCountries.length()){
                    val jsonObjectCountry = jsonArrayCountries.getJSONObject(i)
                    if (jsonObjectCountry.getString("country_name") == country){
                        countryIdSelected = jsonObjectCountry.getString("iso-3166").toString()
                        dialog.dismiss()
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

    private fun initializeComponents() {
        cardViewBack = findViewById(R.id.cardViewBack)
        cardViewFilter = findViewById(R.id.cardViewFilter)
        recyclerViewWorldHoliday = findViewById(R.id.recyclerViewWorldHoliday)

        holidaysList = arrayListOf<HolidaysModel>()

        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setView(R.layout.progress_dialog)
        }.create().also {
            dialog = it
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}