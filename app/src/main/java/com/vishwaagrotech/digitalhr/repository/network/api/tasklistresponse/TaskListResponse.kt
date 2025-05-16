package com.vishwaagrotech.digitalhr.repository.network.api.tasklistresponse

data class TaskListResponse(
    val code: Int,
    val data: List<Data>,
    val status: Boolean
)