package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.LeaveEvent
import com.vishwaagrotech.digitalhr.model.LeaveEventEmployee
import com.vishwaagrotech.digitalhr.repository.LeaveCalendarRepository
import com.vishwaagrotech.digitalhr.repository.mapper.EmployeeLeaveCalendarByDayMapper
import com.vishwaagrotech.digitalhr.repository.mapper.EmployeeLeaveCalendarMapper
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendar.EmployeeLeaveCalendar
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendar.EmployeeLeaveCalendarResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday.EmployeeLeaveCalendarByDay
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday.EmployeeLeaveCalendarByDayResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 *Created by BS on 7/14/2022
 *
 *Copyright (c) 2022, All Rights Reserved.
 */

@HiltViewModel
class LeaveCalendarViewModel @Inject constructor(val repository: LeaveCalendarRepository) :
    ViewModel() {

    private val _leaveCalendar =
        MutableStateFlow<EventHandler<EmployeeLeaveCalendarResponse>>(EventHandler.Empty)

    val leaveCalendar = _leaveCalendar.asStateFlow()

    private val _leaveCalendarByDay =
        MutableStateFlow<EventHandler<EmployeeLeaveCalendarByDayResponse>>(EventHandler.Empty)

    val leaveCalendarByDay = _leaveCalendarByDay.asStateFlow()

    fun getLeaveCalendarResponse() {
        viewModelScope.launch(Dispatchers.IO) {
            _leaveCalendar.value = EventHandler.Loading
            val response = try {
                repository.getEmployeeLeaveCalendarResponse()
            } catch (e: Exception) {
                _leaveCalendar.value = Constant.eventError
                e.printStackTrace()
                return@launch
            }

            if (response.isSuccessful) {
                _leaveCalendar.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _leaveCalendar.value = EventHandler.Failure(responseError.message)
                } catch (e: Exception) {
                    _leaveCalendar.value = Constant.eventError
                }
            }
        }
    }

    fun getLeaveCalendarByDayResponse(day: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _leaveCalendarByDay.value = EventHandler.Loading
            val response = try {
                repository.getEmployeeLeaveCalendarByDayResponse(day)
            } catch (e: Exception) {
                _leaveCalendarByDay.value = Constant.eventError
                e.printStackTrace()
                return@launch
            }

            if (response.isSuccessful) {
                _leaveCalendarByDay.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _leaveCalendarByDay.value = EventHandler.Failure(responseError.message)
                } catch (e: Exception) {
                    _leaveCalendarByDay.value = Constant.eventError
                }
            }
        }
    }

    fun convertDomainToEntityLeaveCalendar(leaves: ArrayList<EmployeeLeaveCalendar>): ArrayList<LeaveEvent> {
        return EmployeeLeaveCalendarMapper.mapToEntityList(leaves) as ArrayList<LeaveEvent>
    }

    fun convertDomainToEntityLeaveCalendarByDay(leaves: ArrayList<EmployeeLeaveCalendarByDay>): ArrayList<LeaveEventEmployee> {
        return EmployeeLeaveCalendarByDayMapper.mapToEntityList(leaves) as ArrayList<LeaveEventEmployee>
    }
}