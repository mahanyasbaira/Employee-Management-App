package com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendar

data class EmployeeLeaveCalendarResponse(
    val `data`: ArrayList<EmployeeLeaveCalendar>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)