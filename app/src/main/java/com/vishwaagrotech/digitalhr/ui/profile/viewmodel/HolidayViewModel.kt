package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.HolidayRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.holiday.Holiday
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
class HolidayViewModel @Inject constructor(private val repository: HolidayRepository) :
    ViewModel() {

    private val _holidayResponse = MutableStateFlow<EventHandler<ArrayList<Holiday>>>(EventHandler.Empty)

    val holidayResponse = _holidayResponse.asStateFlow()

    fun getHolidayResponse() {
        _holidayResponse.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getHolidayResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _holidayResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _holidayResponse.value = EventHandler.Success(response.body()?.data as ArrayList<Holiday>)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _holidayResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun convertHolidayFormat(holidays: ArrayList<Holiday>): ArrayList<com.vishwaagrotech.digitalhr.model.Holiday> {
        val convertedHolidays = ArrayList<com.vishwaagrotech.digitalhr.model.Holiday>()
        for (holiday in holidays) {
            convertedHolidays.add(holiday.convertHolidayFormat(holiday))
        }

        return convertedHolidays
    }

    fun getFilterHolidayList(
        holidays: ArrayList<com.vishwaagrotech.digitalhr.model.Holiday>,
        boolean: Boolean
    ): ArrayList<com.vishwaagrotech.digitalhr.model.Holiday> {
        val holidayList = ArrayList<com.vishwaagrotech.digitalhr.model.Holiday>()
        for (holiday in holidays) {
            val current = Date()
            var date: Date
            val dateFormat = SimpleDateFormat(
                "yyyy-MM-dd"
            )
            date = dateFormat.parse(holiday.date) as Date

            if (boolean) {
                if (date.after(current)) {
                    holidayList.add(holiday)
                }
            } else {
                if (date.before(current)) {
                    holidayList.add(holiday)
                }
            }
        }

        return holidayList
    }
}