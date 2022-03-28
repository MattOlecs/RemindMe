package mateusz.oleksik.remindeme.adapters

import android.widget.TextView
import androidx.databinding.BindingAdapter
import mateusz.oleksik.remindeme.utils.Extensions.Companion.toShortDateString

object DataBindingAdapters {

    @BindingAdapter("long_to_date_formatter")
    @JvmStatic
    fun convertLongToDateTime(textView: TextView, dateNumber: Long) {
        textView.text = dateNumber.toShortDateString()
    }
}