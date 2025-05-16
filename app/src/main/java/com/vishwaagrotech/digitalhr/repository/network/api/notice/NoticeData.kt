package com.vishwaagrotech.digitalhr.repository.network.api.notice

data class NoticeData(
    val description: String,
    val id: Int,
    val notice_published_date: String,
    val notice_title: String
)