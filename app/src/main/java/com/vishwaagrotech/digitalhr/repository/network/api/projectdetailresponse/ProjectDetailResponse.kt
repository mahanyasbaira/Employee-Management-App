package com.vishwaagrotech.digitalhr.repository.network.api.projectdetailresponse

data class ProjectDetailResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)