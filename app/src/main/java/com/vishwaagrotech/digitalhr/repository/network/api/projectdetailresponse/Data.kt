package com.vishwaagrotech.digitalhr.repository.network.api.projectdetailresponse

data class Data(
    val assigned_member: List<AssignedMember>,
    val assigned_task_count: Int,
    val assigned_task_detail: List<AssignedTaskDetail>,
    val attachments: List<Attachment>,
    val client_name: String,
    val cover_pic: String,
    val deadline: String,
    val description: String,
    val id: Int,
    val name: String,
    val priority: String,
    val progress_percent: Int,
    val project_leader: List<ProjectLeader>,
    val start_date: String,
    val status: String
)