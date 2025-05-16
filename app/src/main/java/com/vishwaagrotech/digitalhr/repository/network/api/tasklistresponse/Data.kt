package com.vishwaagrotech.digitalhr.repository.network.api.tasklistresponse

data class Data(
    val assigned_checklists_count: Int,
    val assigned_member: List<AssignedMember>,
    val deadline: String,
    val priority: String,
    val project_name: String,
    val start_date: String,
    val status: String,
    val task_id: Int,
    val task_name: String,
    val task_progress_percent: Int
)