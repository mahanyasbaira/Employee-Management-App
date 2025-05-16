package com.vishwaagrotech.digitalhr.repository.network.api.holiday

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Holiday(
    val description: String?,
    val event: String,
    val event_date: String,
    val id: Int
) {
    fun convertHolidayFormat(holiday: Holiday): com.vishwaagrotech.digitalhr.model.Holiday {
        val fullDate = holiday.event_date
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = format.parse(fullDate)

        val day = DateFormat.format("dd", date) as String
        val monthNumber = DateFormat.format("MMM", date) as String

        return com.vishwaagrotech.digitalhr.model.Holiday(
            holiday.id,
            holiday.event,
            holiday.description,
            monthNumber,
            day,
            fullDate
        )
    }
}