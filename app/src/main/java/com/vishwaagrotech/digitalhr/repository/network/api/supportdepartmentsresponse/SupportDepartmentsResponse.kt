package com.vishwaagrotech.digitalhr.repository.network.api.supportdepartmentsresponse

data class SupportDepartmentsResponse(
    val data: List<Data>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)