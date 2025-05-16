package com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse

data class Checklists(
    val id: Int,
    val is_completed: String,
    val name: String,
    val task_id: String
)