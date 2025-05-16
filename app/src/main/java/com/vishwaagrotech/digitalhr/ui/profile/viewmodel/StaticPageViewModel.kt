package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.StaticPageRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.staticpage.StaticPageResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
*
*Created by BS on 7/13/2022.
*
*Copyright (c) 2022, All Rights Reserved.
*/



@HiltViewModel
class StaticPageViewModel
@Inject
constructor
    (val repository: StaticPageRepository) : ViewModel() {

    private var _staticPageResponse =
        MutableStateFlow<EventHandler<StaticPageResponse>>(EventHandler.Empty)
    var staticPageResponse = _staticPageResponse.asStateFlow()

    fun getStaticPageResponse(value: String = "") {
        viewModelScope.launch {
            _staticPageResponse.value = EventHandler.Loading
            val response = try {
                repository.getStaticPage(value)
            } catch (e: Exception) {
                e.printStackTrace()
                _staticPageResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful){
                _staticPageResponse.value = EventHandler.Success(response.body()!!)
            }else{
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _staticPageResponse.value = EventHandler.Failure(responseError.message)
                } catch (e: Exception) {
                    _staticPageResponse.value = Constant.eventError
                }
            }
        }
    }
}