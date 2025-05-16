package com.vishwaagrotech.digitalhr.repository.network.api.userprofile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("data") val `data`: Profile,
    val message: String,
    val status: Boolean,
    val status_code: Int
)