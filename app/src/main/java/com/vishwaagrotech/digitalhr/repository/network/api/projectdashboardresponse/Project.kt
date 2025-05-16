package com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse

data class Project(
    val assigned_member: List<AssignedMember>,
    val assigned_task_count: Int,
    val end_date: String,
    val id: Int,
    val priority: String,
    val project_name: String,
    val project_progress_percent: Int,
    val start_date: String,
    val status: String
)