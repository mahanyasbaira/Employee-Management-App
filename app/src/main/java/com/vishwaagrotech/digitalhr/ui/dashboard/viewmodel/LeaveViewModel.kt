package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.LeaveRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequest.LeaveRequestResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist.LeaveRequestListResponse
import com.vishwaagrotech.digitalhr.repository.network.api.leavetype.LeaveTypeResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaveViewModel
@Inject
constructor
    (val repository: LeaveRepository) : ViewModel() {

    private val _leaveType: MutableStateFlow<EventHandler<LeaveTypeResponse>> =
        MutableStateFlow(EventHandler.Empty)
    val leaveType = _leaveType.asStateFlow()

    private val _leaveRequest: MutableSharedFlow<EventHandler<LeaveRequestResponse>> =
        MutableSharedFlow()
    val leaveRequest = _leaveRequest.asSharedFlow()

    private val _leaveRequestList: MutableStateFlow<EventHandler<LeaveRequestListResponse>> =
        MutableStateFlow(EventHandler.Empty)
    val leaveRequestList = _leaveRequestList.asStateFlow()

    private val _errorResponse: MutableSharedFlow<String> = MutableSharedFlow()
    val errorResponse = _errorResponse.asSharedFlow()

    suspend fun getStatus() = repository.getActiveStatus()

    fun getLeaveType() {
        viewModelScope.launch {
            val response = try {
                _leaveType.value = EventHandler.Loading
                repository.getLeaveType()
            } catch (e: Exception) {
                _leaveType.value = Constant.eventError
                _errorResponse.emit(Constant.error)
                return@launch
            }

            if (response.isSuccessful) {
                _leaveType.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _leaveType.value = EventHandler.Failure(responseError.message)
                    _errorResponse.emit(responseError.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorResponse.emit(Constant.error)
                    _leaveType.value = Constant.eventError
                }
            }
        }
    }

    fun getLeaveRequestResponse(
        leave_from: String,
        leave_to: String,
        leave_type_id: Int,
        reasons: String,
        early_exit: Int
    ) {
        viewModelScope.launch {
            val response = try {
                _leaveRequest.emit(EventHandler.Loading)
                repository.getLeaveRequest(leave_from, leave_to, leave_type_id, reasons, early_exit)
            } catch (e: Exception) {
                e.printStackTrace()
                _leaveRequest.emit(Constant.eventError)
                return@launch
            }

            if (response.isSuccessful) {
                _leaveRequest.emit(EventHandler.Success(response.body()!!))
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _leaveRequest.emit(EventHandler.Failure(responseError.message))
                } catch (e: Exception) {
                    e.printStackTrace()
                    _leaveRequest.emit(Constant.eventError)
                }
            }
        }
    }

    fun getLeaveRequestListResponse(leave_type: Int, month: Int) {
        viewModelScope.launch {
            val response = try {
                repository.getLeaveRequestList(leave_type, month)
            } catch (e: Exception) {
                _errorResponse.emit(Constant.error)
                _leaveRequestList.value =
                    Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _leaveRequestList.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _leaveRequestList.value = EventHandler.Failure(responseError.message)
                    _errorResponse.emit(responseError.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _errorResponse.emit(Constant.error)
                    _leaveRequestList.value =
                        Constant.eventError
                }
            }
        }
    }
}