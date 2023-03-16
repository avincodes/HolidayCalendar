package lk.nibm.holidaycalendar

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
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
import lk.nibm.calendar.Adapter.HolidaysInMonthAdapter
import lk.nibm.holidaycalendar.Model.HolidaysModel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class LocalHolidaysActivity : AppCompatActivity() {

    private lateinit var cardViewBack: MaterialCardView
    private lateinit var cardViewFilter: MaterialCardView
    private lateinit var recyclerViewLocalHoliday: RecyclerView

    private lateinit var dialog: AlertDialog

    private lateinit var holidaysList: ArrayList<HolidaysModel>

    var countryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_holidays)

        countryId = intent.getStringExtra("countryId").toString()

        initializeComponents()

        getHolidays()

        clickListeners()

    }

    private fun clickListeners() {
        cardViewBack.setOnClickListener {
            onBackPressed()
        }

        cardViewFilter.setOnClickListener {
            openFilterDialogBox()
        }
    }

    private fun openFilterDialogBox() {
        val builder = MaterialAlertDialogBuilder(this)
        val view = layoutInflater.inflate(R.layout.filter_dialog_box, null)
        builder.setCancelable(false)

        val spinnerYear = view.findViewById<Spinner>(R.id.spinnerYear)
        val spinnerMonth = view.findViewById<Spinner>(R.id.spinnerMonth)

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

        builder.setPositiveButton("Filter") { dialog, which ->
            val year = spinnerYear.selectedItem.toString()
            val month = spinnerMonth.selectedItemId.toString()
            getHolidays(year, month)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun getHolidays(year: String, month: String){
        dialog.show()
        holidaysList.clear()

        val url = "https://calendarific.com/api/v2/holidays?api_key=" + resources.getString(R.string.API_KEY) + "&country=" + countryId + "&year=" + year

        val result = StringRequest(Request.Method.GET, url, Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                if (jsonObject != null){
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
                    recyclerViewLocalHoliday.setHasFixedSize(true)
                    recyclerViewLocalHoliday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    recyclerViewLocalHoliday.adapter = adapter
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                } else {
                    holidaysList.clear()
                    dialog.dismiss()
                }

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

    private fun initializeComponents() {
        cardViewBack = findViewById(R.id.cardViewBack)
        cardViewFilter = findViewById(R.id.cardViewFilter)
        recyclerViewLocalHoliday = findViewById(R.id.recyclerViewLocalHoliday)

        holidaysList = arrayListOf<HolidaysModel>()

        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setView(R.layout.progress_dialog)
        }.create().also {
            dialog = it
            dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
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
                recyclerViewLocalHoliday.setHasFixedSize(true)
                recyclerViewLocalHoliday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewLocalHoliday.adapter = adapter
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
}