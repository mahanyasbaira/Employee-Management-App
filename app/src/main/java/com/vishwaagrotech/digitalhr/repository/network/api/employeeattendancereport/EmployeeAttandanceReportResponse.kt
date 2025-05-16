package com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport

data class EmployeeAttandanceReportResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)