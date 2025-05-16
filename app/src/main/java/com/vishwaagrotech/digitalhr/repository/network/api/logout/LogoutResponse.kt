package com.vishwaagrotech.digitalhr.repository.network.api.logout

data class LogoutResponse(
    val status: Boolean,
    val message: String,
    val status_code: Int
)