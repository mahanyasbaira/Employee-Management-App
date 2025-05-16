package com.vishwaagrotech.digitalhr

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.vishwaagrotech.digitalhr.databinding.ActivityMainBinding
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.ui.dashboard.DashboardActivity
import com.vishwaagrotech.digitalhr.ui.form.FormActivity
import com.vishwaagrotech.digitalhr.utils.Constant.TAG
import com.vishwaagrotech.digitalhr.utils.Statusbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    //test
    lateinit var dataStoreManager: DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Statusbar.setStatusbarTheme(this, window, 0, binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel()
        }

    }

    private fun changeActivity() {
        Handler(Looper.getMainLooper()).postDelayed({

            dataStoreManager = DataStoreManager(applicationContext)
            lifecycleScope.launch {
                dataStoreManager.getLoggedIn().collect {
                    if (it) {
                        if (intent.extras != null) {
                            val extra = intent.extras

                            Log.e(TAG, extra?.getString("type").toString())

                            val intent = Intent(this@MainActivity, DashboardActivity::class.java)

                            intent.putExtra("type", extra?.getString("type").toString())

                            if (extra?.getString("type").toString().contentEquals("meeting")) {
                                intent.putExtra("id", extra?.getString("id")?.toInt())
                            }

                            startActivity(intent)
                            finish()
                        } else {
                            startActivity(Intent(this@MainActivity, DashboardActivity::class.java))
                            finish()
                        }
                    } else {
                        startActivity(Intent(applicationContext, FormActivity::class.java))
                        finish()
                    }
                }
            }
        }, 1000)
    }

    override fun onResume() {
        super.onResume()
        changeActivity()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun makeNotificationChannel() {
        val NOTIFICATION_CHANNEL_ID = "CNATTENDANCE"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Attendance",
            NotificationManager.IMPORTANCE_HIGH
        )
        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.beep)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()
        // Configure the notification channel.
        notificationChannel.description = "AttendanceAlert"
        notificationChannel.setSound(soundUri, audioAttributes)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}
