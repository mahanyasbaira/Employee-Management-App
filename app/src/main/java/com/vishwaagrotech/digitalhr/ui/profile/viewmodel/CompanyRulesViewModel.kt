package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.CompanyRulesRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.companyrules.CompanyRulesResponse
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
 *Created by BS on 7/20/2022
 *
 *Copyright (c) 2022, All Rights Reserved.
 */

@HiltViewModel
class CompanyRulesViewModel
@Inject
constructor(val repository: CompanyRulesRepository) : ViewModel() {

    private val _companyRulesResponse =
        MutableStateFlow<EventHandler<CompanyRulesResponse>>(EventHandler.Empty)
    val companyRulesResponse = _companyRulesResponse.asStateFlow()

    init {
        getCompanyRules()
    }

    private fun getCompanyRules() {
        _companyRulesResponse.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getRulesResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _companyRulesResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _companyRulesResponse.value = EventHandler.Success(response.body()!!)
            } else {
                try {
                    val responseError = Gson().fromJson(
                        response.errorBody()?.string(),
                        ErrorResponse::class.java
                    )
                    _companyRulesResponse.value = EventHandler.Failure(responseError.message)
                } catch (e: Exception) {
                    _companyRulesResponse.value = Constant.eventError
                }
            }
        }
    }
}