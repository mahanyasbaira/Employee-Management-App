package com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse

data class ProjectDashboardReponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)