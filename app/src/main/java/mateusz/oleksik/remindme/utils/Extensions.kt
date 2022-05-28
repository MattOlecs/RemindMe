package mateusz.oleksik.remindme.utils

import android.text.format.DateFormat
import java.util.*

class Extensions {

    companion object{
        fun Long.toShortDateString() : String {
            return DateFormat.format("dd/MM/yyyy", Date(this)).toString()
        }

        fun Calendar.compareDateMonthYear(secondDate: Calendar) : Boolean {
            return this.get(Calendar.YEAR) == secondDate.get(Calendar.YEAR)
                    && this.get(Calendar.MONTH) == secondDate.get(Calendar.MONTH)
                    && this.get(Calendar.DAY_OF_MONTH) == secondDate.get(Calendar.DAY_OF_MONTH)
        }
    }
}