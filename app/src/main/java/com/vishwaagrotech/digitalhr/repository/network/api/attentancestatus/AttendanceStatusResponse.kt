package com.vishwaagrotech.digitalhr.repository.network.api.attentancestatus

data class AttendanceStatusResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)