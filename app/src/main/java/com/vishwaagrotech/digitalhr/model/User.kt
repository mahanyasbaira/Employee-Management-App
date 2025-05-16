package com.vishwaagrotech.digitalhr.model

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


data class User(
    var id: Int,
    var username: String,
    var name: String,
    var role: String,
    var image: String,
    var status: Boolean
) {
    constructor() : this(0, "", "", "", "", true)
}
