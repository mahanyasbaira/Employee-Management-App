package com.vishwaagrotech.digitalhr.repository.network.api.holiday

import com.google.gson.annotations.SerializedName

data class HolidayResponse(
    @SerializedName("data") val `data`: List<Holiday>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)