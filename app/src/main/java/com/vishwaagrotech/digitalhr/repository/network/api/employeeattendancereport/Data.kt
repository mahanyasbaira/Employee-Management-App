package com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport

data class Data(
    val employee_attendance: List<EmployeeAttendance>,
    val employee_today_attendance: EmployeeTodayAttendance?,
    val user_detail: UserDetail
)