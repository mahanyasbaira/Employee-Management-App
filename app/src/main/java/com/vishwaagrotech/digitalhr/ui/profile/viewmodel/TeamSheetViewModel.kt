package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.TeamSheetRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.TeamSheetResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamSheetViewModel
@Inject
constructor(private val repository: TeamSheetRepository) : ViewModel() {

    private val _teamSheetResponse =
        MutableStateFlow<EventHandler<TeamSheetResponse>>(EventHandler.Empty)

    val teamSheetResponse = _teamSheetResponse.asStateFlow()

    fun getTeamSheetResponse() {
        _teamSheetResponse.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getTeamResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _teamSheetResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _teamSheetResponse.value = EventHandler.Success(response.body()!!)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _teamSheetResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }
}