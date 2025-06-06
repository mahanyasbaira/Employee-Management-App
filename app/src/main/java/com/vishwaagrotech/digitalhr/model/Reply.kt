package com.vishwaagrotech.digitalhr.model

data class Reply(
    val avatar: String,
    val comment_id: String,
    val created_at: String,
    val created_by_id: String,
    val created_by_name: String,
    val description: String,
    val mentioned: ArrayList<Mentioned>,
    val reply_id: Int,
    val username: String
)