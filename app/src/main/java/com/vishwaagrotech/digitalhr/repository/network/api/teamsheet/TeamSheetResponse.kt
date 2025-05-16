package com.vishwaagrotech.digitalhr.repository.network.api.teamsheet

data class TeamSheetResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)