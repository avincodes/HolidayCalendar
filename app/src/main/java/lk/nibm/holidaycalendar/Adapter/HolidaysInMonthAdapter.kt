package lk.nibm.calendar.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import lk.nibm.holidaycalendar.Model.HolidaysModel
import lk.nibm.holidaycalendar.R

class HolidaysInMonthAdapter(var context: Context, var holidayList: List<HolidaysModel>) : RecyclerView.Adapter<HolidaysInMonthAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.holiday_item, parent, false))
    }

    override fun getItemCount(): Int {
        return holidayList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

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

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHolidayDate: TextView = itemView.findViewById(R.id.txtHolidayDate)
        val txtHolidayTitle: TextView = itemView.findViewById(R.id.txtHolidayTitle)
        val txtHolidayDescription: TextView = itemView.findViewById(R.id.txtHolidayDescription)
        val txtMonth: TextView = itemView.findViewById(R.id.txtMonth)
        val txtYear: TextView = itemView.findViewById(R.id.txtYear)
    }

}