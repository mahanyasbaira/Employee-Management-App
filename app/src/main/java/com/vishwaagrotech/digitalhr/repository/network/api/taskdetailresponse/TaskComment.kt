package com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse

data class TaskComment(
    val avatar: String,
    val created_at: String,
    val created_by_id: String,
    val created_by_name: String,
    val description: String,
    val id: Int,
    val mentioned: List<Mentioned>,
    val replies: List<Reply>,
    val username: String
)