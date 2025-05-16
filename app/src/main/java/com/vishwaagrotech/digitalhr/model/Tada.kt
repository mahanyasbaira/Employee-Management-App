package com.vishwaagrotech.digitalhr.model

data class Tada(
    val employee: String,
    val id: Int,
    val remark: String,
    val status: String,
    val submitted_date: String,
    val title: String,
    val total_expense: String,
    val description: String = "",
    val verified_by: String = "",
    val attachments: ArrayList<Attachment> = ArrayList()
) {
    constructor(
        employee: String,
        id: Int,
        remark: String,
        status: String,
        submitted_date: String,
        title: String,
        total_expense: String
    ) : this(employee, id, remark, status, submitted_date, title, total_expense, "","", ArrayList())
}
