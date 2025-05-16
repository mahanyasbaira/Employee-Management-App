package com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse

data class Progress(
    val progress_in_percent: Int,
    val total_task_assigned: Int,
    val total_task_completed: Int
)