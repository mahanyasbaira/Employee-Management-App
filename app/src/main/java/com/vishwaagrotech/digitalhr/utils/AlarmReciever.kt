package com.vishwaagrotech.digitalhr.utils

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.vishwaagrotech.digitalhr.R
import com.google.firebase.messaging.FirebaseMessagingService

/**
 *Copyright (c) 2022, All Rights Reserved.
 */
class AlarmReciever : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = 101
        const val NOTIFICATION_CHANNEL_ID = "DIGITALHR"
    }

    override fun onReceive(p0: Context?, p1: Intent?) {

    }

    private fun notifyUser(context: Context, title: String, message: String) {

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)

        val notificationManager =
            context.getSystemService(FirebaseMessagingService.NOTIFICATION_SERVICE) as NotificationManager

        val id = System.currentTimeMillis().toInt()


        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_notification)
            .setTicker("Hearty365")
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(message)
            .setContentInfo("Info")

        notificationManager.notify(id, notificationBuilder.build())
    }
}