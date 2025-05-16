package com.vishwaagrotech.digitalhr.repository.network.api.savecommentresponse

data class SaveCommentResponse(
    val data: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)