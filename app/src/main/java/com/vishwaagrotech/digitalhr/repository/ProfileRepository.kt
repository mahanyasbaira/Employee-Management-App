package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getProfitResponse() = retrofitService.getUserProfileResponse("Bearer ${getToken()}")

    suspend fun getProfileDetailResponse(id: String) =
        retrofitService.getEmployeeDetail("Bearer ${getToken()}", id)

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun getUser() = dataStoreManager.getUser().first()

    suspend fun updateProfilePicture(avatar: MultipartBody.Part) =
        retrofitService.updateProfilePicture("Bearer ${getToken()}", avatar)
}