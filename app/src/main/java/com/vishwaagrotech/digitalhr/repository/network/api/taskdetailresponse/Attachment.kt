package com.vishwaagrotech.digitalhr.repository.network.api.taskdetailresponse

data class Attachment(
    val attachment_url: String,
    val extension: String,
    val file_name: String,
    val id: Int,
    val type: String
)