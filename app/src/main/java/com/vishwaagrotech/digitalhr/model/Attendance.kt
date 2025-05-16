package com.vishwaagrotech.digitalhr.model

/**
*Copyright (c) 2022, All Rights Reserved.
*/


data class Attendance(
    var id: Int,
    var date: String,
    var day: String,
    var startTime: String,
    var endTime: String
)
