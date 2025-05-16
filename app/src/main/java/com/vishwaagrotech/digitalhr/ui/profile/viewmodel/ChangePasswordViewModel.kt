package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.ChangePasswordRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.changepassword.ChangePasswordResponse
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
 *Created by BS on 7/18/2022
 *
 *Copyright (c) 2022, All Rights Reserved.
 */
@HiltViewModel
class ChangePasswordViewModel
@Inject constructor(
    val repository: ChangePasswordRepository
) : ViewModel() {
    private var _changePassword =
        MutableStateFlow<EventHandler<ChangePasswordResponse>>(EventHandler.Empty)
    var changePassword = _changePassword.asStateFlow()

    fun getChangePasswordResponse(old: String, new: String, confirm: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _changePassword.value = EventHandler.Loading
            val response = try {
                repository.getChangePasswordResponse(old, new, confirm)
            } catch (e: Exception) {
                _changePassword.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _changePassword.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _changePassword.value = EventHandler.Failure(responseError.message)
                } catch (e: Exception) {
                    _changePassword.value = Constant.eventError
                }
            }
        }
    }

    fun validateEmptyField(value: String): String {
        if (value.isBlank() && value.length < 5) {
            return "The field is blank or less than required character"
        }

        return ""
    }

    fun validateNewPasswordField(new: String, confirm: String): String {
        if (!new.contentEquals(confirm)) {
            return "The password doesn't match with the confirm password"
        }

        return ""
    }
}