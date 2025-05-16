package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class TadaRepository
@Inject
constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getTadaListResponse() =
        retrofitService.getTadaList("Bearer ${getToken()}")

    suspend fun getTadaDetailResponse(value: String) =
        retrofitService.getTadaDetail("Bearer ${getToken()}", value)

    suspend fun saveTadaDetailResponse(
        title: RequestBody,
        desc: RequestBody,
        expense: RequestBody,
        files: List<MultipartBody.Part>
    ) =
        retrofitService.saveTadaCreate("Bearer ${getToken()}", title, desc, expense, files)

    suspend fun editTadaDetailResponse(
        value: RequestBody,
        title: RequestBody,
        desc: RequestBody,
        expense: RequestBody,
        files: List<MultipartBody.Part>
    ) =
        retrofitService.editTadaCreate("Bearer ${getToken()}", value, title, desc, expense, files)

    suspend fun deleteTadaAttachmentResponse(
        value: String,
    ) =
        retrofitService.deleteAttachments("Bearer ${getToken()}", value)

    suspend fun getToken() = dataStoreManager.getToken().first()
}