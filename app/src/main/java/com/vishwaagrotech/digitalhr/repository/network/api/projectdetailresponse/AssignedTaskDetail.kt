package com.vishwaagrotech.digitalhr.repository.network.api.projectdetailresponse

data class AssignedTaskDetail(
    val deadline: String,
    val priority: String,
    val start_date: String,
    val status: String,
    val task_id: Int,
    val task_name: String,
    val task_progress_percent: Int
)