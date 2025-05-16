package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HeaderRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {

    suspend fun getUserDetail() = dataStoreManager.getUser().first()
}