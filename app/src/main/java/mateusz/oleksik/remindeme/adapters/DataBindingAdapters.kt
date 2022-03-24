package mateusz.oleksik.remindeme.adapters

import android.text.format.DateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

object DataBindingAdapters {

    @BindingAdapter("long_to_date_formatter")
    @JvmStatic
    fun convertLongToDateTime(textView: TextView, dateNumber: Long) {
        val date = Date(dateNumber)
        textView.text = DateFormat.format("dd-MM-yyyy", date).toString()
    }
}