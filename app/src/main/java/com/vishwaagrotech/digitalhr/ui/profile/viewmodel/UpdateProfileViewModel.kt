package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.UpdateProfileRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.userprofile.ProfileResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 *
 *Created by BS on 7/20/2022
 *
 *Copyright (c) 2022, All Rights Reserved.
 */

@HiltViewModel
class UpdateProfileViewModel
@Inject
constructor(
    val repository: UpdateProfileRepository
) : ViewModel() {

    private val _profileUpdate: MutableSharedFlow<EventHandler<ProfileResponse>> =
        MutableSharedFlow()
    val profileUpdate = _profileUpdate.asSharedFlow()

    fun updateProfilePicture(
        name: String,
        email: String,
        address: String,
        phone: String,
        gender: String,
        dob: String
    ) {
        viewModelScope.launch {

            _profileUpdate.emit(EventHandler.Loading)
            val response = try {
                repository.updateProfile(name, email, address, phone, gender, dob)
            } catch (e: Exception) {
                e.printStackTrace()
                _profileUpdate.emit(Constant.eventError)
                return@launch
            }

            if (response.isSuccessful) {
                _profileUpdate.emit(EventHandler.Success(response.body()!!))
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _profileUpdate.emit(EventHandler.Failure(responseError.message))
            }
        }
    }

    fun validateEmptyField(value: String): Boolean {
        return value.isNotBlank()
    }

    fun isEmailValid(value: String): Boolean {
        return !TextUtils.isEmpty(value) && android.util.Patterns.EMAIL_ADDRESS.matcher(value)
            .matches()
    }
}