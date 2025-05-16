package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HomeRepository
    @Inject constructor(
        private val retrofitService: RetrofitService,
        private val dataStoreManager: DataStoreManager
) {

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun storeUser(user: com.vishwaagrotech.digitalhr.repository.network.api.login.User) = dataStoreManager.storeUser(user)

    suspend fun getSecurityCheck() = dataStoreManager.getSecurityCheck().first()

    suspend fun storeActiveStatus(boolean : Boolean) = dataStoreManager.storeActiveStatus(boolean)

    suspend fun getUser() = dataStoreManager.getUser().first()

    suspend fun clearData() = dataStoreManager.clearAllData()

    suspend fun getCheckInResponse(
        token: String,
        lan: Double,
        long: Double,
        bssid: String,
    ) = retrofitService.getCheckInResponse("Bearer $token", bssid, lan, long)

    suspend fun getCheckOutResponse(
        token: String,
        lan: Double,
        long: Double,
        bssid: String,
    ) = retrofitService.getCheckOutResponse("Bearer $token", bssid, lan, long)

    suspend fun getDashboard(
        token: String
    ) = retrofitService.getDashboard("Bearer $token")

}