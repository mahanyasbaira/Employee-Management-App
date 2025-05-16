package com.vishwaagrotech.digitalhr.repository.network.api.commentlistresponse

data class CommentListResponse(
    val code: Int,
    val data: List<Data>,
    val status: Boolean
)