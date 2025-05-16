package com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday

data class EmployeeLeaveCalendarByDayResponse(
    val `data`: ArrayList<EmployeeLeaveCalendarByDay>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)