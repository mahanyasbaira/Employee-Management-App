package com.vishwaagrotech.digitalhr.repository.network.api.teamsheet

data class Employee(
    val avatar: String,
    val department: String,
    val dob: String,
    val email: String,
    val gender: String,
    val id: Int,
    val name: String,
    val phone: Long,
    val post: String,
    val online_status : Int
)