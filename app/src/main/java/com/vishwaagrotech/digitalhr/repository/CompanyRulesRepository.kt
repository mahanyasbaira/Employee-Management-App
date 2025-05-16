package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 *Copyright (c) 2022, All Rights Reserved.
 */
class CompanyRulesRepository
@Inject
constructor(
    val retrofitService: RetrofitService,
    val dataStoreManager: DataStoreManager
) {

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun getRulesResponse() = retrofitService.getCompanyRulesResponse("Bearer ${getToken()}")
}