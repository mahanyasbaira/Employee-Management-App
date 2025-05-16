package com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist

data class LeaveRequestList(
    val admin_remark: String,
    val early_exit: Boolean,
    val id: Int,
    val leave_from: String,
    val leave_reason: String,
    val leave_requested_date: String,
    val leave_to: String,
    val leave_type_id: Int,
    val leave_type_name: String,
    val no_of_days: Int,
    val status: String,
    val status_updated_by: String
)