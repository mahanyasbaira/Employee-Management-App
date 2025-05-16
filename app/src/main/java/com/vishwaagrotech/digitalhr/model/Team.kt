package com.vishwaagrotech.digitalhr.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(val id: Int, val name: String, val post: String, val image: String)  :Parcelable
