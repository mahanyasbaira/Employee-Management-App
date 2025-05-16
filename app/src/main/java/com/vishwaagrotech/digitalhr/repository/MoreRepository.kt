package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MoreRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getLogoutResponse() =
        retrofitService.getLogoutResponse("Bearer ${getToken()}")

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun clearData() = dataStoreManager.clearAllData()

    suspend fun getSecurityCheck() = dataStoreManager.getSecurityCheck().first()

    suspend fun storeSecurityCheck(boolean: Boolean) = dataStoreManager.storeSecurityCheck(boolean)

}