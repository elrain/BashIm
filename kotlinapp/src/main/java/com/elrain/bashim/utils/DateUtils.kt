package com.elrain.bashim.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {

        private val DATE_FORMATS: Array<String> = arrayOf("EEE, dd MMM yyyy H:mm:ss",
                "dd.MM.yy H:mm", "yyyy-MM-dd H:mm")

        fun parseDateFromString(stringDate: String): Date {
            var d = Date()

            val sdf = SimpleDateFormat("", Locale.US)
            for (format in DATE_FORMATS) {
                sdf.applyPattern(format)
                try {
                    d = sdf.parse(stringDate)
                    break
                } catch (e: ParseException) {
                    continue
                }
            }

            return d
        }

        fun getItemPubDate(date: Date): String {
            val c = Calendar.getInstance()
            c.time = date
            return "${c.get(Calendar.YEAR)}-${isZeroNeeded(c.get(Calendar.MONTH) + 1)}" +
                    "-${isZeroNeeded(c.get(Calendar.DAY_OF_MONTH))} ${isZeroNeeded(date.hours)}" +
                    ":${isZeroNeeded(c.get(Calendar.MINUTE))}"
        }

        private fun isZeroNeeded(value: Int): String {
            return if (value < 10) "0$value" else value.toString()
        }
    }
}