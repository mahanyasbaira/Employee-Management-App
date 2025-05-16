package com.vishwaagrotech.digitalhr.repository.network.api.login

data class User(
    val avatar: String,
    val email: String,
    val id: Int,
    val name: String,
    val username: String,
    val active_status :Boolean
)