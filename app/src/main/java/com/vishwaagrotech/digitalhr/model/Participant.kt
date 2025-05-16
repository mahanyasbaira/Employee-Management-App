package com.vishwaagrotech.digitalhr.model

data class Participant(
    val avatar: String,
    val email: String,
    val id: String,
    val name: String,
    val online_status: String,
    val phone: Long,
    val post: String
)