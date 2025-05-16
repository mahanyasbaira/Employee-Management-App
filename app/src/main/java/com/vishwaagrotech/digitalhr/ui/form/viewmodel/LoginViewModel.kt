package com.vishwaagrotech.digitalhr.ui.form.viewmodel

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.LoginRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.login.LoginResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.DeviceUuid
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _loginResponse: MutableStateFlow<EventHandler<LoginResponse>> =
        MutableStateFlow(EventHandler.Empty)
    val loginResponse = _loginResponse.asStateFlow()

    suspend fun loginUser(email: String, password: String) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            getLoginResponse(email, password, it)
        }
        var fcm: String = ""
        fcm = repository.dataStoreManager.getFCMToken().first()

    }

    suspend fun rememberMe() = repository.getRememberMe()

    private fun getLoginResponse(email: String, password: String, fcm: String) {
        viewModelScope.launch {
            _loginResponse.value = EventHandler.Loading
            val response = try {
                repository.getLoginResponse(
                    email,
                    password,
                    "android",
                    DeviceUuid.getUniquePsuedoID(),
                    fcm
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _loginResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                try {
                    _loginResponse.value = EventHandler.Success(response.body()!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    _loginResponse.value =
                        Constant.eventError
                }
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loginResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun checkValidEmail(value: String): Boolean {
        return !TextUtils.isEmpty(value)
    }

    fun checkPassword(value: String): Boolean {
        if (value.isNotEmpty()) {
            if (value.length > 5) {
                return true
            }
        }
        return false
    }

    fun storeUserCacheDetail(user: LoginResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.storeLoginUserDetail(user)
        }
    }

    fun storeRememberMe(value: String) {
        viewModelScope.launch {
            repository.storeRememberMe(value)
        }
    }

}