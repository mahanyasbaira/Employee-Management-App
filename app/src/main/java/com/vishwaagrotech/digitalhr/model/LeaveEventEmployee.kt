package com.vishwaagrotech.digitalhr.model

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


data class LeaveEventEmployee(
    var id: Int,
    var image: String,
    var name: String,
    var role: String,
    var days: Int,
    var status: String
)
