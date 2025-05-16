package com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse

data class TaskDetailResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)