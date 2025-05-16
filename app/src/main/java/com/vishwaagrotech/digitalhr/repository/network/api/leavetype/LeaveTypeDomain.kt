package com.vishwaagrotech.digitalhr.repository.network.api.leavetype

data class LeaveTypeDomain(
    val leave_taken: Int,
    val leave_type_id: Int,
    val leave_type_name: String,
    val leave_type_slug : String,
    val leave_type_status: Boolean,
    val total_leave_allocated: Int,
    val early_exit : Boolean
)