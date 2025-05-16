package com.vishwaagrotech.digitalhr.repository.network.api.leavetype

data class LeaveTypeResponse(
    val `data`: List<LeaveTypeDomain>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)