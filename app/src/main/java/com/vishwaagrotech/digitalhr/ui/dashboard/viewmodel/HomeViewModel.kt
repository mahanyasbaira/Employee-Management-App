package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.User
import com.vishwaagrotech.digitalhr.repository.HomeRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.attentancestatus.Data
import com.vishwaagrotech.digitalhr.repository.network.api.dashboard.DashboardResponse
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _dashboardResponse: MutableStateFlow<EventHandler<DashboardResponse>> =
        MutableStateFlow(EventHandler.Empty)
    val dashboardResponse: StateFlow<EventHandler<DashboardResponse>> = _dashboardResponse

    private val _errorResponse: MutableSharedFlow<String> = MutableSharedFlow()
    val errorResponse = _errorResponse.asSharedFlow()

    private val _securityCheck = MutableLiveData<Boolean>()
    private val _person = MutableLiveData<User>()

    private val _checkInResponse: MutableSharedFlow<EventHandler<Data>> = MutableSharedFlow()
    val checkInResponse = _checkInResponse.asSharedFlow()

    private val _checkOutResponse: MutableSharedFlow<EventHandler<Data>> = MutableSharedFlow()
    val checkOutResponse = _checkOutResponse.asSharedFlow()

    val securityCheck: LiveData<Boolean>
        get() = _securityCheck

    val person: LiveData<User>
        get() = _person

    fun getSecurityCheck() {
        viewModelScope.launch {
            _securityCheck.postValue(repository.getSecurityCheck())
        }
    }

    fun getUserDetail() {
        viewModelScope.launch {
            _person.postValue(repository.getUser())
        }
    }

    fun getDashboard() {
        _dashboardResponse.value = EventHandler.Loading
        viewModelScope.launch(Dispatchers.IO) {
            dashboardResponse(repository.getToken())
        }
    }

    private suspend fun dashboardResponse(token: String) {
        viewModelScope.launch {
            val response = try {
                repository.getDashboard(token)
            } catch (e: Exception) {
                _dashboardResponse.value = EventHandler.Empty
                _errorResponse.emit("Something went wrong, Try again later.")
                return@launch
            }

            if (response.isSuccessful) {
                Log.e("Response", response.body()!!.data.toString())
                val userObj = response.body()!!.data.user;
                val user = com.vishwaagrotech.digitalhr.repository.network.api.login.User(
                    userObj.avatar,
                    userObj.email,
                    userObj.id,
                    userObj.name,
                    userObj.username,
                    userObj.online_status
                )
                updateUserDetail(user)

                _dashboardResponse.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _dashboardResponse.value = EventHandler.Empty
                    _errorResponse.emit(responseError.message)
                } catch (e: Exception) {
                    _errorResponse.emit("Something went wrong. Try again later.")
                }
            }
        }
    }

    fun callUserCheckedIn(bssid: String, lat: Double, long: Double) {
        viewModelScope.launch {
            val token = repository.getToken()
            userCheckInResponse(token, bssid, lat, long)
        }
    }

    private fun userCheckInResponse(token: String, bssid: String, lat: Double, long: Double) {
        viewModelScope.launch {
            _checkInResponse.emit(EventHandler.Loading)
            delay(3000)
            val response = try {
                repository.getCheckInResponse(token, lat, long, bssid)
            } catch (e: Exception) {
                _checkInResponse.emit(EventHandler.Failure("Something went wrong, Try again later."))
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
        }
    }

    fun callUserCheckedOut(bssid: String, lat: Double, long: Double) {
        viewModelScope.launch {
            val token = repository.getToken()
            userCheckOutResponse(token, bssid, lat, long)
        }
    }

    private fun userCheckOutResponse(token: String, bssid: String, lat: Double, long: Double) {
        viewModelScope.launch {
            _checkOutResponse.emit(EventHandler.Loading)
            delay(3000)
            val response = try {
                repository.getCheckOutResponse(token, lat, long, bssid)
            } catch (e: Exception) {
                _checkOutResponse.emit(EventHandler.Failure("Something went wrong, Try again later."))
                return@launch
            }

            if (response.isSuccessful) {
                _checkOutResponse.emit(EventHandler.Success(response.body()?.data!!))
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _checkOutResponse.emit(EventHandler.Failure(responseError.message))
                } catch (e: Exception) {
                    e.printStackTrace()
                    _checkOutResponse.emit(EventHandler.Failure("Something went wrong, Try again later."))
                }
            }
        }
    }

    fun updateUserDetail(user: com.vishwaagrotech.digitalhr.repository.network.api.login.User) {
        viewModelScope.launch {
            repository.storeUser(user)
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

    fun clearDatastore() {
        viewModelScope.launch {
            repository.clearData()
        }
    }
}