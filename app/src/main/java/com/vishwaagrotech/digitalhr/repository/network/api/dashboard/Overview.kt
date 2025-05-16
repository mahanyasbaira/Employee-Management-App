package com.vishwaagrotech.digitalhr.repository.network.api.dashboard

data class Overview(
    val present_days: Int,
    val total_holidays: Int,
    val total_leave_taken: Int,
    val total_paid_leaves: Int,
    val total_pending_leaves: Int,
    val total_assigned_projects: Int,
    val total_pending_tasks: Int
)