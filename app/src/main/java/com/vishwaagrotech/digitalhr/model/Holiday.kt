package com.vishwaagrotech.digitalhr.model

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


data class Holiday(
    var id: Int,
    var title: String,
    var message: String?,
    var month: String,
    var day: String,
    var date: String
)
