package com.vishwaagrotech.digitalhr.repository.network.api.employeedetailresponse

data class EmployeeDetailResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)