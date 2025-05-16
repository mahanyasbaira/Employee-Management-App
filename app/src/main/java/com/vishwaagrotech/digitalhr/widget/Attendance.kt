package com.vishwaagrotech.digitalhr.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.vishwaagrotech.digitalhr.MainActivity
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.repository.HomeRepository
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.attentancestatus.Data
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.widget.network.RetrofitWidget
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


/**
 * Implementation of App Widget functionality.
 */

lateinit var checkInIntent: Intent
lateinit var checkInPendingIntent: PendingIntent

lateinit var checkOutIntent: Intent
lateinit var checkOutPendingIntent: PendingIntent

lateinit var noticeIntent: Intent
lateinit var noticePendingIntent: PendingIntent

lateinit var notificationIntent: Intent
lateinit var notificationPendingIntent: PendingIntent

lateinit var repository: HomeRepository

private val _checkInResponse = MutableSharedFlow<EventHandler<Data>>()
val checkInResponse = _checkInResponse.asSharedFlow()

private val _checkOutResponse = MutableSharedFlow<EventHandler<Data>>()
val checkOutResponse = _checkOutResponse.asSharedFlow()

lateinit var fusedLocationClient: FusedLocationProviderClient

private val ACTION_BUTTON_CHECK_IN = "ACTION_BUTTON_CHECK_IN"
private val ACTION_BUTTON_CHECK_OUT = "ACTION_BUTTON_CHECK_OUT"
private val ACTION_BUTTON_NOTICE = "ACTION_BUTTON_NOTICE"
private val ACTION_BUTTON_NOTIFICATION = "ACTION_BUTTON_NOTIFICATION"

class Attendance : AppWidgetProvider() {

    companion object {
        var api = false
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        observeCheckIn(context)
        observeCheckOut(context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        checkInIntent = Intent(context, Attendance::class.java)
            .setAction(ACTION_BUTTON_CHECK_IN)
        checkInPendingIntent = PendingIntent.getBroadcast(
            context, 0, checkInIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        checkOutIntent = Intent(context, Attendance::class.java)
            .setAction(ACTION_BUTTON_CHECK_OUT)
        checkOutPendingIntent = PendingIntent.getBroadcast(
            context, 0, checkOutIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        noticeIntent = Intent(context, Attendance::class.java)
            .setAction(ACTION_BUTTON_NOTICE)
        noticePendingIntent = PendingIntent.getBroadcast(
            context, 0, noticeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        notificationIntent = Intent(context, Attendance::class.java)
            .setAction(ACTION_BUTTON_NOTIFICATION)
        notificationPendingIntent = PendingIntent.getBroadcast(
            context, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        api = false
    }

    override fun onDisabled(context: Context) {
        api = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent != null) {
            if (intent.action.equals(ACTION_BUTTON_CHECK_IN)) {
                if (!api) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            if (it.longitude == 0.0 && it.latitude == 0.0) {
                                Toast.makeText(
                                    context,
                                    "Location not detected. Either location is off or hardware issue occurred.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                checkIn(context, it.longitude, it.latitude)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Location not detected. Either location is off or hardware issue occurred.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            } else if (intent.action.equals(ACTION_BUTTON_CHECK_OUT)) {
                if (!api) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            if (it.longitude == 0.0 && it.latitude == 0.0) {
                                Toast.makeText(
                                    context,
                                    "Location not detected. Either location is off or hardware issue occurred.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                checkOut(context, it.longitude, it.latitude)
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Location not detected. Either location is off or hardware issue occurred.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            } else if (intent.action.equals(ACTION_BUTTON_NOTIFICATION)) {
                context?.startActivity(
                    Intent(context, MainActivity::class.java).putExtra(
                        "type",
                        "notification"
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else if (intent.action.equals(ACTION_BUTTON_NOTICE)) {
                context?.startActivity(
                    Intent(context, MainActivity::class.java).putExtra(
                        "type",
                        "notice"
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            } else {
                super.onReceive(context, intent)
            }
        }
    }

    fun observeCheckIn(context: Context) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            checkInResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        api = false
                    }
                    is EventHandler.Failure -> {
                        api = false
                        Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                    }
                    is EventHandler.Loading -> {
                        api = true
                    }
                    is EventHandler.Success -> {
                        api = false
                        Toast.makeText(context, "Checked In", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun observeCheckOut(context: Context) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            checkOutResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        api = false
                    }
                    is EventHandler.Failure -> {
                        api = false
                        Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                    }
                    is EventHandler.Loading -> {
                        api = true
                    }
                    is EventHandler.Success -> {
                        api = false
                        Toast.makeText(context, "Checked Out", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.attendance).also { view ->
        view.setOnClickPendingIntent(R.id.appwidget_check_in, checkInPendingIntent)
        view.setOnClickPendingIntent(R.id.appwidget_check_out, checkOutPendingIntent)
        view.setOnClickPendingIntent(R.id.appwidget_notification, notificationPendingIntent)
        view.setOnClickPendingIntent(R.id.appwidget_notice, noticePendingIntent)

        view.setOnClickPendingIntent(
            R.id.layout_widget,
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        //view.setBoolean(R.id.appwidget_check_in, "setEnabled", false)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

internal fun checkIn(context: Context?, longat: Double, lat: Double) {
    val retrofitWidget = RetrofitWidget.getInstance()
    val dataStoreManager = DataStoreManager(context!!)

    CoroutineScope(Dispatchers.Main.immediate).launch {
        _checkInResponse.emit(EventHandler.Loading)
        if (!dataStoreManager.getToken().first().contentEquals("")) {
            val response = try {
                retrofitWidget.getCheckInResponse(
                    "Bearer ".plus(dataStoreManager.getToken().first()),
                    getCurrentNetworkDetail(context),
                    lat.toDouble(),
                    longat.toDouble()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _checkInResponse.emit(EventHandler.Failure("Something went wrong."))
                return@launch
            }


            if (response.isSuccessful) {
                _checkInResponse.emit(EventHandler.Success(response.body()?.data!!))
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _checkInResponse.emit(EventHandler.Failure(responseError.message))
            }
        } else {
            _checkInResponse.emit(EventHandler.Failure("You can't use this feature until you are logged in."))
        }

    }

}

internal fun checkOut(context: Context?, long: Double, lat: Double) {
    val retrofitWidget = RetrofitWidget.getInstance()
    val dataStoreManager = DataStoreManager(context!!)

    CoroutineScope(Dispatchers.Main.immediate).launch {
        _checkOutResponse.emit(EventHandler.Loading)
        if (!dataStoreManager.getToken().first().contentEquals("")) {
            val response = try {
                retrofitWidget.getCheckOutResponse(
                    "Bearer ".plus(dataStoreManager.getToken().first()),
                    getCurrentNetworkDetail(context),
                    84.1654654,
                    24.23465464
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _checkOutResponse.emit(EventHandler.Failure("Something went wrong."))
                return@launch
            }


            if (response.isSuccessful) {
                _checkOutResponse.emit(EventHandler.Success(response.body()?.data!!))
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _checkOutResponse.emit(EventHandler.Failure(responseError.message))
            }
        } else {
            _checkOutResponse.emit(EventHandler.Failure("You can't use this feature until you are logged in."))
        }
    }

}

private fun getCurrentNetworkDetail(context: Context): String {
    val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    if (wifiManager != null) {
        val bssid = wifiManager.connectionInfo.bssid
        if (bssid.isNullOrEmpty()) {
            return ""
        }
        Log.e("Bssid", bssid)

        return bssid
    }

    return ""
}