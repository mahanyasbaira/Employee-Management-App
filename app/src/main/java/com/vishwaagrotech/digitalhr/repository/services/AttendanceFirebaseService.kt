package com.vishwaagrotech.digitalhr.repository.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.ui.dashboard.DashboardActivity
import com.vishwaagrotech.digitalhr.utils.DigitalHR
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AttendanceFirebaseService : FirebaseMessagingService() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        DigitalHR.appScope.launch {
            dataStoreManager.storeFCMToken(token)
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data["type"].contentEquals("meeting")) {
            createNotificationDefault(
                remoteMessage.data["title"],
                remoteMessage.data["message"],
                remoteMessage.data["type"],
                remoteMessage.data["id"]!!
            )
        } else {
            createNotificationDefault(
                remoteMessage.data["title"],
                remoteMessage.data["message"],
                remoteMessage.data["type"]
            )
        }

    }

    private fun createNotificationDefault(
        title: String?,
        body: String?,
        type: String?,
        value_id: String = "0"
    ) {
        val NOTIFICATION_CHANNEL_ID = "CNATTENDANCE"

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val id = System.currentTimeMillis().toInt()

        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_notification)
            .setTicker("Hearty365")
            .setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent(type, value_id.toInt()))
            .setContentText(body)
            .setContentInfo("Info")

        notificationManager.notify(id, notificationBuilder.build())

    }

    private fun pendingIntent(type: String?, id: Int = 0): PendingIntent {
        when (type) {
            "notification" -> {
                return NavDeepLinkBuilder(this)
                    .setComponentName(DashboardActivity::class.java)
                    .setGraph(R.navigation.dashboard_nav_graph)
                    .setDestination(R.id.notificationFragment2)
                    .createPendingIntent()
            }
            "notice" -> {
                return NavDeepLinkBuilder(this)
                    .setComponentName(DashboardActivity::class.java)
                    .setGraph(R.navigation.dashboard_nav_graph)
                    .setDestination(R.id.noticeFragment2)
                    .createPendingIntent()
            }

            "meeting" -> {
                return NavDeepLinkBuilder(this)
                    .setComponentName(DashboardActivity::class.java)
                    .setGraph(R.navigation.dashboard_nav_graph)
                    .setDestination(R.id.meetingDetailFragment2)
                    .setArguments(bundleOf("meeting_id" to id))
                    .createPendingIntent()
            }

            "leave" -> {
                return NavDeepLinkBuilder(this)
                    .setComponentName(DashboardActivity::class.java)
                    .setGraph(R.navigation.leave_nav_graph)
                    .setDestination(R.id.leaveFragment)
                    .createPendingIntent()
            }
            else -> {
                return NavDeepLinkBuilder(this)
                    .setComponentName(DashboardActivity::class.java)
                    .setGraph(R.navigation.dashboard_nav_graph)
                    .setDestination(R.id.homeFragment)
                    .createPendingIntent()
            }
        }
    }
}