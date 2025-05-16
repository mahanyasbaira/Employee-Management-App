package com.vishwaagrotech.digitalhr.repository.network.api.notification

data class NoticationResponse(
    val status_code: Int,
    val `data`: List<Notification>,
    val links: Links,
    val status: Boolean
)