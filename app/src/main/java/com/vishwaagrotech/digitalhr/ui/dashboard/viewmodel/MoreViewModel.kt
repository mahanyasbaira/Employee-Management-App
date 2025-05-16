package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.MoreRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.logout.LogoutResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(private val repository: MoreRepository) : ViewModel() {

    private val _logoutResponse = MutableStateFlow<EventHandler<LogoutResponse>>(EventHandler.Empty)
    val logoutResponse = _logoutResponse.asStateFlow()

    val errorMessage = MutableLiveData<ErrorResponse>()

    private val _securityCheck = MutableLiveData<Boolean>()

    val securityCheck: LiveData<Boolean>
        get() = _securityCheck

    fun getLogoutResponse() {
        viewModelScope.launch {
            _logoutResponse.value = EventHandler.Loading
            val response = try {
                repository.getLogoutResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _logoutResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _logoutResponse.value = EventHandler.Success(response.body()!!)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                if (responseError.status_code == 401){
                    errorMessage.value = responseError
                }
                _logoutResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun storeSecurityCheck(boolean: Boolean) {
        viewModelScope.launch {
            repository.storeSecurityCheck(boolean)

            getSecurityCheck()
        }
    }

    fun getSecurityCheck() {
        viewModelScope.launch {
            _securityCheck.postValue(repository.getSecurityCheck())
        }
    }

    fun clearDatastore() {
        viewModelScope.launch {
            repository.clearData()
        }
    }
}