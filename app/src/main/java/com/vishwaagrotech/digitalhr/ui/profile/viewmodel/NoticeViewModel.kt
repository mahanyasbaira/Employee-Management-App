package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Notice
import com.vishwaagrotech.digitalhr.repository.NoticeRepository
import com.vishwaagrotech.digitalhr.repository.mapper.NoticeMapper
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.notice.NoticeData
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(val repository: NoticeRepository) :
    ViewModel() {

    private val _noticeResponse =
        MutableStateFlow<EventHandler<ArrayList<NoticeData>>>(EventHandler.Empty)
    private val _errorResponse = MutableLiveData<ErrorResponse>()

    val noticeResponse = _noticeResponse.asStateFlow()

    val errorResponse: LiveData<ErrorResponse>
        get() = _errorResponse

    private fun getNoticeResponse(per_page: Int, page: Int) {
        viewModelScope.launch {
            val response = try {
                repository.getNoticeResponse(per_page, page)
            } catch (e: Exception) {
                e.printStackTrace()
                _noticeResponse.value = Constant.eventError
                return@launch
            }

            if (response.isSuccessful) {
                _noticeResponse.value =
                    EventHandler.Success(response.body()?.data as ArrayList<NoticeData>)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _noticeResponse.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun fetchNotification(per_page: Int, page: Int) {
        viewModelScope.launch {
            getNoticeResponse(per_page, page)
        }
    }

    fun convertNotice(notices: ArrayList<NoticeData>): ArrayList<Notice> {
        val noticeList = ArrayList<Notice>()
        noticeList.addAll(NoticeMapper.mapToEntityList(notices))
        return noticeList
    }

}