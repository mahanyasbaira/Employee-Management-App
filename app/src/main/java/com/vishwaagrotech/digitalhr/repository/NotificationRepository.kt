package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getNotificationResponse(per_page: Int, page: Int) =
        retrofitService.getNotification("Bearer ${getToken()}", per_page, page)

    suspend fun getToken() = dataStoreManager.getToken().first()
}