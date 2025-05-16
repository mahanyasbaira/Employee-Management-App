package com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse

data class AssignedTask(
    val assigned_member: List<AssignedMember>,
    val end_date: String,
    val priority: String,
    val project_name: String,
    val start_date: String,
    val status: String,
    val task_id: Int,
    val task_name: String
)