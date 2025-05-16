package com.vishwaagrotech.digitalhr.repository.network.api.supportlistresponse

data class SupportListResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)