package com.vishwaagrotech.digitalhr.model

data class Comment(
    val avatar: String,
    val created_at: String,
    val created_by_id: String,
    val created_by_name: String,
    val description: String,
    val id: Int,
    val mentioned: ArrayList<Mentioned>,
    var replies: ArrayList<Reply>,
    val username: String
)