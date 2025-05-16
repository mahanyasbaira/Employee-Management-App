package com.vishwaagrotech.digitalhr.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

@HiltAndroidApp
class DigitalHR : Application() {

    companion object{
        var appScope = MainScope()
    }

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        TypefaceUtil.overrideFont(this, "SERIF", "fonts/google_sans.ttf")

        FirebaseApp.initializeApp(this)
        FirebaseInstallations.getInstance().id.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
            }
        }


    }

    override fun onLowMemory() {
        super.onLowMemory()
        appScope.cancel("Low Memory in app")
        appScope = MainScope()
    }
}