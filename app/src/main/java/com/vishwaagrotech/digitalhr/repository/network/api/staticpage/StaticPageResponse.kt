package com.vishwaagrotech.digitalhr.repository.network.api.staticpage

data class StaticPageResponse(
    val `data`: Data,
    val message: String,
    val status: Boolean,
    val status_code: Int
)