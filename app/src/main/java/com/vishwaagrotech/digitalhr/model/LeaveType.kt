package com.vishwaagrotech.digitalhr.model

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


data class LeaveType(
    var id: Int,
    var title: String,
    var total: Int,
    var used: Int,
    var early_exit: Boolean = false,
    var status: Boolean = true
) {
    override fun toString(): String {
        return title
    }

    fun id(): Int {
        return id
    }
}
