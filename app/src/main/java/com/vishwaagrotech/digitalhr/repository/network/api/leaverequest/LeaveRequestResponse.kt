package com.vishwaagrotech.digitalhr.repository.network.api.leaverequest

data class LeaveRequestResponse(
    val message: String,
    val status: Boolean,
    val status_code: Int
)