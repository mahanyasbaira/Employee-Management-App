package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.EmployeeAttendanceReportRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport.EmployeeAttandanceReportResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val employeeAttendanceReportRepository: EmployeeAttendanceReportRepository
) : ViewModel() {

    private val _attendance: MutableStateFlow<EventHandler<EmployeeAttandanceReportResponse>> =
        MutableStateFlow(EventHandler.Empty)

    val attendance = _attendance.asStateFlow()

    fun getCurrentDate(): String {
        val date = Date()
        return DateFormat.format("MM", date) as String
    }

    init {
        attendanceResponse(Integer.parseInt(getCurrentDate()))
    }

    fun attendanceResponse(value: Int) {
        viewModelScope.launch {
            _attendance.value = EventHandler.Loading
            val response = try {
                employeeAttendanceReportRepository.getReportResponse(value)
            } catch (e: Exception) {
                e.printStackTrace()
                _attendance.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _attendance.value = EventHandler.Success(response.body()!!)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _attendance.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun checkAttendanceHour(start: String, end: String): String {
        if (!start.contentEquals("-")) {
            val format = SimpleDateFormat("h:mm a", Locale.US)
            val startdate = format.parse(start)

            if (!end.contentEquals("-")) {
                val endDate = format.parse(end)
                val diff: Long = endDate.time - startdate.time

                val second = diff / 1000.toFloat()
                val min = second / 60
                val minGone = (min % 60).toInt()
                val hour: Int = (min / 60).toInt()
                return "$hour hr $minGone min"
            } else {
                val date = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                val enddate = SimpleDateFormat("h:mm a", Locale.getDefault()).parse(date)

                val diff: Long = enddate.time - startdate.time

                val second = diff / 1000.toFloat()
                val min = second / 60
                val minGone = (min % 60).toInt()
                val hour: Int = (min / 60).toInt()
                return "$hour hr $minGone min"
            }
        }

        return "0hr 0min"
    }

    fun checkAttendanceMinute(start: String, end: String): Float {
        if (!start.contentEquals("-")) {
            val format = SimpleDateFormat("h:mm a", Locale.US)
            val startdate = format.parse(start)

            if (!end.contentEquals("-")) {
                val endDate = format.parse(end)
                val diff: Long = endDate.time - startdate.time

                val second = diff / 1000
                val min = (second / 60)
                val progress = (min / 480.toFloat() * 100.toFloat())
                Log.e("Progress", progress.toString())
                return progress.toFloat()
            } else {
                val date = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
                val enddate = SimpleDateFormat("h:mm a", Locale.getDefault()).parse(date)

                val diff: Long = enddate.time - startdate.time

                val second = diff / 1000
                val min = second / 60

                val progress = (min / 480.toFloat() * 100.toFloat())

                Log.e("Progress", progress.toString())
                return progress.toFloat()
            }
        }

        return 0f
    }
}