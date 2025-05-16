package com.vishwaagrotech.digitalhr.model

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


data class Leave(
    var id: Int,
    var title: String,
    var message: String,
    var startDate: String,
    var engDate: String,
    var status: Int,
    var createdAt: String,
    var updatedBy: String = ""
)
