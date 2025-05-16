package com.vishwaagrotech.digitalhr.repository.network.api.userprofile

data class Profile(
    val address: String,
    val avatar: String,
    val bank_account_no: Long,
    val bank_account_type: String,
    val bank_name: String,
    val branch: String,
    val department: String,
    val dob: String,
    val email: String,
    val employment_type: String,
    val gender: String,
    val id: Int,
    val joining_date: String,
    val leave_allocated: Int,
    val name: String,
    val office_time: String,
    val phone: Long,
    val post: String,
    val role: String,
    val status: String,
    val user_type: String,
    val username: String
)