package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TeamSheetRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getTeamResponse() = retrofitService.getTeamResponse("Bearer ${getToken()}")

    suspend fun getToken() = dataStoreManager.getToken().first()
}