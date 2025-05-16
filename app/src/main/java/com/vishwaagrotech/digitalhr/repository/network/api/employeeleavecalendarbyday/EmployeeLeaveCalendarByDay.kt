package com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday

data class EmployeeLeaveCalendarByDay(
    val department: String,
    val leave_days: Int,
    val leave_from: String,
    val leave_id: Int,
    val leave_status: String,
    val leave_to: String,
    val post: String,
    val user_avatar: String,
    val user_id: Int,
    val user_name: String
)