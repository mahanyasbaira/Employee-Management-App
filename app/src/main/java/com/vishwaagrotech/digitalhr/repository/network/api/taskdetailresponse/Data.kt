package com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse

data class Data(
    val assigned_member: List<AssignedMember>,
    val attachments: List<Attachment>,
    val checklists: List<Checklists>,
    val deadline: String,
    val description: String,
    val has_checklist: Boolean,
    val priority: String,
    val project_name: String,
    val start_date: String,
    val status: String,
    val task_comments: List<TaskComment>,
    val task_id: Int,
    val task_name: String,
    val task_progress_percent: Int
)