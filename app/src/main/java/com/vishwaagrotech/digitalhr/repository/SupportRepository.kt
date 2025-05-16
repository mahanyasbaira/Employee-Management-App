package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SupportRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getSupportDepartmentsResponse() =
        retrofitService.getSupportDepartmentsResponse("Bearer ${getToken()}")

    suspend fun getSupportListResponse() =
        retrofitService.getSupportListResponse("Bearer ${getToken()}")

    suspend fun saveSupportResponse(title: String, description: String, departmentId: Int) =
        retrofitService.saveSupportResponse(
            "Bearer ${getToken()}",
            title,
            description,
            departmentId.toString()
        )

    suspend fun getToken() = dataStoreManager.getToken().first()
}