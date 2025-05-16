package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.repository.ProfileRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.employeedetailresponse.Data
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileDetailViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {

    private val _userResponse = MutableStateFlow<EventHandler<Data>>(EventHandler.Empty)

    val user = MutableLiveData<Data>()

    fun getProfileDetailResponse(id : String) {
        _userResponse.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getProfileDetailResponse(id)
            } catch (e: Exception) {
                e.printStackTrace()
                _userResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _userResponse.value = EventHandler.Success(response.body()!!.data)
                user.postValue(response.body()?.data)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _userResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }
}