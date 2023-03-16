package lk.nibm.calendar.Adapter

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import lk.nibm.holidaycalendar.Model.HolidaysModel
import lk.nibm.holidaycalendar.R
import java.util.*

class HolidayAdapter(var context: Context, var holidayList: List<HolidaysModel>) : RecyclerView.Adapter<HolidayAdapter.MyViewHolder>() {

    lateinit var speech: TextToSpeech

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.holiday_item, parent, false))
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HolidayAdapter.MyViewHolder, position: Int) {

        holder.txtHolidayDate.text = holidayList[position].holidayDate
        holder.txtHolidayTitle.text = holidayList[position].holidayName
        holder.txtHolidayDescription.text = holidayList[position].holidayDescription

        // Set Month
        val month = holidayList[position].holidayMonth
        when (month) {
            "1" -> holder.txtMonth.text = "Jan"
            "2" -> holder.txtMonth.text = "Feb"
            "3" -> holder.txtMonth.text = "Mar"
            "4" -> holder.txtMonth.text = "April"
            "5" -> holder.txtMonth.text = "May"
            "6" -> holder.txtMonth.text = "June"
            "7" -> holder.txtMonth.text = "July"
            "8" -> holder.txtMonth.text = "Aug"
            "9" -> holder.txtMonth.text = "Sept"
            "10" -> holder.txtMonth.text = "Oct"
            "11" -> holder.txtMonth.text = "Nov"
            "12" -> holder.txtMonth.text = "Dec"
        }

        // Set Year
        holder.txtYear.text = holidayList[position].holidayYear

    }

    override fun getItemCount(): Int {
        return holidayList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHolidayDate: TextView = itemView.findViewById(R.id.txtHolidayDate)
        val txtHolidayTitle: TextView = itemView.findViewById(R.id.txtHolidayTitle)
        val txtHolidayDescription: TextView = itemView.findViewById(R.id.txtHolidayDescription)
        val txtMonth: TextView = itemView.findViewById(R.id.txtMonth)
        val txtYear: TextView = itemView.findViewById(R.id.txtYear)
    }
}