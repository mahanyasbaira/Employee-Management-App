package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Notice
import com.vishwaagrotech.digitalhr.repository.NotificationRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.notification.Notification
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(val repository: NotificationRepository) :
    ViewModel() {

    private val _notificationResponse =
        MutableStateFlow<EventHandler<ArrayList<Notification>>>(EventHandler.Empty)
    private val _errorResponse = MutableLiveData<ErrorResponse>()

    val notificationResponse = _notificationResponse.asStateFlow()

    val errorResponse: LiveData<ErrorResponse>
        get() = _errorResponse

    private fun getNotificationResponse(per_page: Int, page: Int) {
        viewModelScope.launch {
            val response = try {
                repository.getNotificationResponse(per_page, page)
            } catch (e: Exception) {
                e.printStackTrace()
                _notificationResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _notificationResponse.value = EventHandler.Success(response.body()?.data as ArrayList<Notification>)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _notificationResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun fetchNotification(per_page: Int, page: Int) {
        viewModelScope.launch {
            getNotificationResponse(per_page, page)
        }
    }

    fun convertNotification(notifications: ArrayList<Notification>): ArrayList<Notice> {
        val noticeList = ArrayList<Notice>()

        for (notification in notifications) {
            noticeList.add(notification.convertNotificationFormat(notification))
        }

        return noticeList
    }

}