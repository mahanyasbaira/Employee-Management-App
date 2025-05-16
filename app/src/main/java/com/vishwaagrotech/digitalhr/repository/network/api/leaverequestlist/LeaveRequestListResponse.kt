package com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist

data class LeaveRequestListResponse(
    val `data`: List<LeaveRequestList>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)