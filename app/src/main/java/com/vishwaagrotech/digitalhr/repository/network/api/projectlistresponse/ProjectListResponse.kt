package com.vishwaagrotech.digitalhr.repository.network.api.projectlistresponse

data class ProjectListResponse(
    val code: Int,
    val data: List<Data>,
    val status: Boolean
)