package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EmployeeAttendanceReportRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val retrofitService: RetrofitService
) {

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun getReportResponse(value: Int) =
        retrofitService.getEmployeeAttendanceReport("Bearer ${getToken()}", value)
}