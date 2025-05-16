package com.vishwaagrotech.digitalhr.repository.network.api.dashboard

data class User(
    val avatar: String,
    val email: String,
    val id: Int,
    val name: String,
    val username: String,
    val online_status : Boolean
)