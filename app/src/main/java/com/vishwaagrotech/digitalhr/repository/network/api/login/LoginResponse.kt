package com.vishwaagrotech.digitalhr.repository.network.api.login

data class LoginResponse(
    val `data`: Data,
    val status: Boolean,
    val message: String,
    val status_code: Int
)