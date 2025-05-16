package com.vishwaagrotech.digitalhr.repository.network.api.projectlistresponse

data class Data(
    val assigned_member: List<AssignedMember>,
    val assigned_task_count: Int,
    val deadline: String,
    val id: Int,
    val name: String,
    val priority: String,
    val progress_percent: Int,
    val start_date: String,
    val status: String
)