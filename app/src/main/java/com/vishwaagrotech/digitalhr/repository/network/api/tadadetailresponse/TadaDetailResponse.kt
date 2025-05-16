package com.vishwaagrotech.digitalhr.repository.network.api.tadadetailresponse

data class TadaDetailResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)