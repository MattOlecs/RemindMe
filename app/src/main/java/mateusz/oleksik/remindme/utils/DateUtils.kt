package mateusz.oleksik.remindme.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object{
        private val supportedFormats = listOf("dd-MM-yyyy", "dd:MM:yyyy", "dd/MM/yyyy", "dd.MM.yyyy")

        fun tryParseDate(dateString: String): Date? {
            for (format in supportedFormats){
                try {
                    return SimpleDateFormat(format).parse(dateString)
                }
                catch (ex: ParseException) {}
            }

            return null
        }
    }
}