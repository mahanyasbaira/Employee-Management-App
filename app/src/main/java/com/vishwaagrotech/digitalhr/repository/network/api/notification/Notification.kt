package com.vishwaagrotech.digitalhr.repository.network.api.notification

import android.text.format.DateFormat
import com.vishwaagrotech.digitalhr.model.Notice
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val description: String,
    val id: Int,
    val notification_published_date: String,
    val notification_title: String
){
    fun convertNotificationFormat(notification: Notification): Notice {
        val fullDate = notification.notification_published_date
        val format = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
        val date = format.parse(fullDate)

        val day = DateFormat.format("dd", date) as String
        val monthNumber = DateFormat.format("MMM", date) as String

        return Notice(
            notification.id,
            notification.notification_title,
            notification.description,
            monthNumber,
            day
        )
    }
}