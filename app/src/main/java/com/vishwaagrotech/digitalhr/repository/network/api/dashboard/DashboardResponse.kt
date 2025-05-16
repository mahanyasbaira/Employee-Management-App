package com.vishwaagrotech.digitalhr.repository.network.api.dashboard

data class DashboardResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)