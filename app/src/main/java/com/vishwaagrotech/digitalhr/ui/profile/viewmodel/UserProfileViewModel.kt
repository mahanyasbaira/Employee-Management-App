package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.User
import com.vishwaagrotech.digitalhr.repository.ProfileRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.userprofile.Profile
import com.vishwaagrotech.digitalhr.repository.network.api.userprofile.ProfileResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(private val repository: ProfileRepository) :
    ViewModel() {

    private val _userResponse = MutableStateFlow<EventHandler<Profile>>(EventHandler.Empty)
    private val _localUserDetail = MutableLiveData<User>()
    private val _profileUpdate: MutableSharedFlow<EventHandler<ProfileResponse>> =
        MutableSharedFlow()

    val profileUpdate = _profileUpdate.asSharedFlow()

    val userResponse = _userResponse.asStateFlow()

    val user = MutableLiveData<Profile>()

    val localUserDetail: LiveData<User>
        get() = _localUserDetail

    fun getProfileResponse() {
        _userResponse.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getProfitResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _userResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _userResponse.value = EventHandler.Success(response.body()?.data!!)
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

    fun getStoreValue() {
        viewModelScope.launch {
            _localUserDetail.postValue(repository.getUser())
        }
    }

    fun updateProfilePicture(file: File) {
        viewModelScope.launch {
            val body: MultipartBody.Part = MultipartBody.Part.createFormData(
                "avatar",
                file.name,
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )

            _profileUpdate.emit(EventHandler.Loading)
            val response = try {
                repository.updateProfilePicture(body)
            } catch (e: Exception) {
                e.printStackTrace()
                _profileUpdate.emit(Constant.eventError)
                return@launch
            }

            if (response.isSuccessful) {
                _profileUpdate.emit(EventHandler.Success(response.body()!!))
                _userResponse.value = EventHandler.Success(response.body()?.data!!)
                user.postValue(response.body()?.data)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _profileUpdate.emit(EventHandler.Failure(responseError.message))
            }
        }
    }
}