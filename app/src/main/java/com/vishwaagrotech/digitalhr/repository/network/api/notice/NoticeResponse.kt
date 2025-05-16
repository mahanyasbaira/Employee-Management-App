package com.vishwaagrotech.digitalhr.repository.network.api.notice

data class NoticeResponse(
    val `data`: ArrayList<NoticeData>,
    val status: Boolean,
    val status_code: Int
)