package com.vishwaagrotech.digitalhr.utils

import java.text.SimpleDateFormat
import java.util.*

object Constant {
    //Your URL/LIVE_URL is inserted here for api and other purpose
    const val APP_URL: String = "http://192.168.29.210:8001/"

    const val TAG = "FORTESTPURPOSE"

    const val error = "Something went wrong. Try again later"

    val eventError = EventHandler.Failure(error)

    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

}