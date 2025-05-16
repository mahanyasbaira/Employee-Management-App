package com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse

data class Data(
    val assigned_task: List<AssignedTask>,
    val progress: Progress,
    val projects: List<Project>
)