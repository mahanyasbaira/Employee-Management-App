package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Meeting
import com.vishwaagrotech.digitalhr.repository.TeamMeetingRepository
import com.vishwaagrotech.digitalhr.repository.mapper.TeamMeetingMapper
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.teammeeting.TeamMeeting
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamMeetingViewModel @Inject constructor(val repository: TeamMeetingRepository) :
    ViewModel() {

    private val _teamMeetingResponse =
        MutableStateFlow<EventHandler<ArrayList<Meeting>>>(EventHandler.Empty)
    private val _errorResponse = MutableLiveData<ErrorResponse>()

    val teamMeetingResponse = _teamMeetingResponse.asStateFlow()

    val errorResponse: LiveData<ErrorResponse>
        get() = _errorResponse

    private fun getTeamMeetingResponse(value: String, per_page: Int, page: Int) {
        viewModelScope.launch {
            val response = try {
                repository.getTeamMeetingResponse(value, page, per_page)
            } catch (e: Exception) {
                e.printStackTrace()
                _teamMeetingResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _teamMeetingResponse.value = EventHandler.Success(
                    TeamMeetingMapper.mapToEntityList(
                        response.body()!!.data as ArrayList<TeamMeeting>
                    ) as ArrayList<Meeting>
                )
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _teamMeetingResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun fetchMeeting(value: String, per_page: Int, page: Int) {
        viewModelScope.launch {
            getTeamMeetingResponse(value, per_page, page)
        }
    }
}