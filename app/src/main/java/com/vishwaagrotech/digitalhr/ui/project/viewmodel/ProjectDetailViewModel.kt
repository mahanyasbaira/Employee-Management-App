package com.vishwaagrotech.digitalhr.ui.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.model.Project
import com.vishwaagrotech.digitalhr.model.Task
import com.vishwaagrotech.digitalhr.model.Team
import com.vishwaagrotech.digitalhr.repository.ProjectRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel
@Inject
constructor(private val repository: ProjectRepository) : ViewModel() {

    private val _projectDetail =
        MutableStateFlow<Project>(Project())
    val projectDetail = _projectDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun getTaskListOverview(value: String) {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                repository.getProjectDetail(value)
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                val value = response.body()!!.data

                val teamList = ArrayList<Team>()
                for (team in value.assigned_member) {
                    teamList.add(Team(team.id, team.name, team.post, team.avatar))
                }

                val leaderList = ArrayList<Team>()
                for (team in value.project_leader) {
                    leaderList.add(Team(team.id, team.name, team.post, team.avatar))
                }

                val attachments = ArrayList<Attachment>()
                for (attachment in value.attachments) {
                    attachments.add(Attachment(attachment.id, attachment.attachment_url,attachment.type))
                }

                val tasks = ArrayList<Task>()
                for (task in value.assigned_task_detail) {
                    tasks.add(
                        Task(
                            task.task_id,
                            task.task_name,
                            "",
                            task.start_date,
                            task.deadline,
                            value.name,
                            task.status,
                            "",
                            teamList,
                            value.assigned_task_count.toString(),
                            arrayListOf(),
                            arrayListOf(),
                            false
                        )
                    )
                }

                val project = Project(
                    value.id,
                    value.name,
                    value.description,
                    value.start_date,
                    value.deadline,
                    value.assigned_task_count.toString(),
                    value.priority,
                    value.progress_percent.toString(),
                    value.status,
                    teamList,
                    leaderList,
                    attachments,
                    tasks
                )

                _projectDetail.value = project

                _loading.value = false
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loading.value = false
            }
        }
    }

}