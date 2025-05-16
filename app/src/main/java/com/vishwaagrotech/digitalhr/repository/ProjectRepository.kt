package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProjectRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getProjectOverview(task: Int, project: Int) =
        retrofitService.getProjectDashboard(
            "Bearer ${getToken()}",
            task.toString(),
            project.toString()
        )

    suspend fun getTaskList() =
        retrofitService.getTaskList(
            "Bearer ${getToken()}",
        )

    suspend fun getProjectList() =
        retrofitService.getProjectList(
            "Bearer ${getToken()}",
        )

    suspend fun getProjectDetail(value: String) =
        retrofitService.getProjectDetail(
            "Bearer ${getToken()}",
            value
        )

    suspend fun getTaskDetail(value: String) =
        retrofitService.getTaskDetail(
            "Bearer ${getToken()}",
            value
        )

    suspend fun toggleChecklist(value: String) =
        retrofitService.toggleChecklist(
            "Bearer ${getToken()}",
            value
        )

    suspend fun toggleTask(value: String) =
        retrofitService.toggleTask(
            "Bearer ${getToken()}",
            value
        )

    suspend fun getToken() = dataStoreManager.getToken().first()
}