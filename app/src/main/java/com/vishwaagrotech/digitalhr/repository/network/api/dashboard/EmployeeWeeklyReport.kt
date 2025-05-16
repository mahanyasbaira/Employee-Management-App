package com.vishwaagrotech.digitalhr.repository.network.api.dashboard

data class EmployeeWeeklyReport(
    val attendance_date: String,
    val check_in: String,
    val check_out: String,
    val productive_time_in_min: Int,
    val week_day: String,
    val week_day_in_number: String
)