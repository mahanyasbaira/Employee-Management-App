package com.vishwaagrotech.digitalhr.model

import com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse.Project
import com.vishwaagrotech.digitalhr.repository.network.api.projectlistresponse.Data

data class Project(
    val id: Int,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val totalTask: String,
    val priority: String,
    val progress: String,
    val status: String,
    val teams: List<Team>,
    val leader: List<Team>,
    val attachment: List<Attachment>,
    val task: List<Task>,
) {
    companion object {
        fun parseData(projects: List<Project>): ArrayList<com.vishwaagrotech.digitalhr.model.Project> {
            var projectList: ArrayList<com.vishwaagrotech.digitalhr.model.Project> = ArrayList()
            for (project in projects) {
                var teamList: ArrayList<Team> = ArrayList()
                for (team in project.assigned_member) {
                    teamList.add(Team(team.id, team.name, "", team.avatar))
                }
                projectList.add(
                    Project(
                        project.id,
                        project.project_name,
                        "",
                        project.start_date,
                        project.end_date,
                        project.assigned_task_count.toString(),
                        project.priority,
                        project.project_progress_percent.toString(),
                        project.status,
                        teamList, arrayListOf(), arrayListOf(), arrayListOf()
                    )
                )
            }
            return projectList;
        }

        fun parseDataList(projects: List<Data>): ArrayList<com.vishwaagrotech.digitalhr.model.Project> {
            var projectList: ArrayList<com.vishwaagrotech.digitalhr.model.Project> = ArrayList()
            for (project in projects) {
                var teamList: ArrayList<Team> = ArrayList()
                for (team in project.assigned_member) {
                    teamList.add(Team(team.id, team.name, "", team.avatar))
                }
                projectList.add(
                    Project(
                        project.id,
                        project.name,
                        "",
                        project.start_date,
                        project.deadline,
                        project.assigned_task_count.toString(),
                        project.priority,
                        project.progress_percent.toString(),
                        project.status,
                        teamList, arrayListOf(), arrayListOf(), arrayListOf()
                    )
                )
            }
            return projectList;
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
        "",
        arrayListOf(),
        arrayListOf(),
        arrayListOf(),
        arrayListOf()
    )
}
