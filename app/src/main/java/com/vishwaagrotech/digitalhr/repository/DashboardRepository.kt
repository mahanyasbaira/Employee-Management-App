package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    val retrofitService: RetrofitService,
    val dataStoreManager: DataStoreManager
) {

    suspend fun getToken() = dataStoreManager.getToken().first()

}