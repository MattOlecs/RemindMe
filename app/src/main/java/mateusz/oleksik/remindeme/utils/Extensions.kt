package mateusz.oleksik.remindeme.utils

import android.text.format.DateFormat
import java.util.*

class Extensions {

    companion object{
        fun Long.toShortDateString() : String {
            return DateFormat.format("dd/MM/yyyy", Date(this)).toString()
        }
    }
}