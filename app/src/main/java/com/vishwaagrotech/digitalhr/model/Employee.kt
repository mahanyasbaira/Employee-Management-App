package com.vishwaagrotech.digitalhr.model

import com.vishwaagrotech.digitalhr.R

/**
*Copyright (c) 2022, All Rights Reserved.
*/


data class Employee(
    var id: Int,
    var name: String,
    var role: String,
    var image: Int,
    var status: Boolean
) {
    constructor() : this(0, "", "", R.drawable.placeholder, true)
}
