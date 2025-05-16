package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 *
 *Created by BS on 7/20/2022
 *
 *Copyright (c) 2022, All Rights Reserved.
 */
class UpdateProfileRepository @Inject constructor(
    val retrofitService: RetrofitService,
    val dataStoreManager: DataStoreManager
) {

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun updateProfile(
        name: String,
        email: String,
        address: String,
        phone: String,
        gender: String,
        dob: String
    ) = retrofitService.updateProfile("Bearer ${getToken()}", name, email, address, phone, gender,dob)

    suspend fun storeUser(user: com.vishwaagrotech.digitalhr.repository.network.api.login.User) =
        dataStoreManager.storeUser(user)
}