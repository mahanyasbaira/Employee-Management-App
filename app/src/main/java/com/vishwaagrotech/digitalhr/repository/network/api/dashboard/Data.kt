package com.vishwaagrotech.digitalhr.repository.network.api.dashboard

data class Data(
    val employee_today_attendance: EmployeeTodayAttendance,
    val user: User,
    var overview: Overview,
    var employee_weekly_report: ArrayList<EmployeeWeeklyReport>,
    var office_time: OfficeTime,
    var shift_dates: ArrayList<String>
)