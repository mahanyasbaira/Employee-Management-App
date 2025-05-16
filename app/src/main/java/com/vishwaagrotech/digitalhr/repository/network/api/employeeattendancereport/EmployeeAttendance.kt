package com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport


data class EmployeeAttendance(
    val id: Int,
    val attendance_date: String,
    val check_in: String,
    val check_out: String,
    val week_day: String
)