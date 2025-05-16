package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 *Copyright (c) 2022, All Rights Reserved.
 */
class ChangePasswordRepository
@Inject constructor(val retrofitService: RetrofitService, val dataStoreManager: DataStoreManager) {

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun getChangePasswordResponse(
        old: String,
        new: String,
        confirm: String,
    ) = retrofitService.changePassword("Bearer ${getToken()}", old, new, confirm)
}