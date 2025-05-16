package com.vishwaagrotech.digitalhr.model

import com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse.AssignedTask
import com.vishwaagrotech.digitalhr.repository.network.api.tasklistresponse.Data

data class Task(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val projectName: String,
    val status: String,
    val priority: String,
    val team: ArrayList<Team>,
    val commentCount: String,
    val checklist: ArrayList<Checklist>,
    val attachment: ArrayList<Attachment>,
    val hasCheckList: Boolean,
) {
    companion object {
        fun parseData(taskResponse: List<AssignedTask>): ArrayList<Task> {
            val taskList: ArrayList<Task> = ArrayList()
            for (task in taskResponse) {
                taskList.add(
                    Task(
                        task.task_id,
                        task.task_name,
                        "",
                        task.start_date,
                        task.end_date,
                        task.project_name,
                        task.status,
                        "",
                        arrayListOf(),
                        "0",
                        arrayListOf(),
                        arrayListOf(),
                        false
                    )
                )
            }
            return taskList
        }

        fun parseDataList(taskResponse: List<Data>): ArrayList<Task> {
            val taskList: ArrayList<Task> = ArrayList()
            for (task in taskResponse) {
                taskList.add(
                    Task(
                        task.task_id,
                        task.task_name,
                        "",
                        task.start_date,
                        task.deadline,
                        task.project_name,
                        task.status,
                        "",
                        arrayListOf(),
                        "0",
                        arrayListOf(),
                        arrayListOf(),
                        false
                    )
                )
            }
            return taskList
        }
    }

    constructor() : this(
        0,
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        arrayListOf(),
        "",
        arrayListOf(),
        arrayListOf(),
        false
    )
}
