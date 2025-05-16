package com.vishwaagrotech.digitalhr.repository.network

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("status_code") val status_code: Int
)