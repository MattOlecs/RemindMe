package mateusz.oleksik.remindeme.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import mateusz.oleksik.remindeme.Food
import mateusz.oleksik.remindeme.R
import mateusz.oleksik.remindeme.interfaces.FoodCreateDialogListener
import java.sql.Date
import java.time.LocalDate

class FoodCreateFragment(
    private val listener: FoodCreateDialogListener
    ) : DialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_create_food, container, false)

        val cancelButton = view.findViewById<Button>(R.id.food_create_cancel_button)
        val createButton = view.findViewById<Button>(R.id.food_create_confirm_button)
        val nameTextView = view.findViewById<TextView>(R.id.food_create_name_text_view)
        var calendarView = view.findViewById<CalendarView>(R.id.food_create_calendar_view)


        calendarView.setOnDateChangeListener{calendarView, year, month, dayOfMonth ->

            calendarView.date = getDateFromInt(year, month, dayOfMonth)
        }

        cancelButton.setOnClickListener{
            dismiss()
        }

        createButton.setOnClickListener{
            val foodName = nameTextView.text.toString()
            val expirationDate = calendarView.date


            listener.onCreatedFood(Food(0, foodName, expirationDate))
            dismiss()
        }

        return view
    }

    private fun getDateFromInt(year: Int, month: Int, dayOfMonth: Int) : Long{
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)

        return calendar.time.time
    }
}