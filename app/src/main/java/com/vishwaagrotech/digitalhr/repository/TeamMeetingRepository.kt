package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TeamMeetingRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getTeamMeetingResponse(value: String, page: Int, per_page: Int) =
        retrofitService.getTeamMeeting("Bearer ${getToken()}", value, per_page, page)

    suspend fun getToken() = dataStoreManager.getToken().first()
}