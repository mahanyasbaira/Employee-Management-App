package com.vishwaagrotech.digitalhr.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Support(
    val id: Int,
    val title: String,
    val description: String,
    val issueAt: String,
    val status: String,
    val approvedBy: String,
    val approvedAt: String,
    val createdAt: String,
    val day: String,
    val month: String,
) : Parcelable
