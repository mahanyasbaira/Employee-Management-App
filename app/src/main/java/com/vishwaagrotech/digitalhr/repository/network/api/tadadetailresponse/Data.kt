package com.vishwaagrotech.digitalhr.repository.network.api.tadadetailresponse

data class Data(
    val attachments: Attachments,
    val description: String,
    val employee: String,
    val id: Int,
    val remark: String,
    val status: String,
    val submitted_date: String,
    val title: String,
    val total_expense: String,
    val verified_by: String
)