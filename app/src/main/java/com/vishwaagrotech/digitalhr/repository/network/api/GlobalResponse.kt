package com.vishwaagrotech.digitalhr.repository.network.api

data class GlobalResponse(
    val message: String,
    val status: Boolean,
    val status_code: Int
)