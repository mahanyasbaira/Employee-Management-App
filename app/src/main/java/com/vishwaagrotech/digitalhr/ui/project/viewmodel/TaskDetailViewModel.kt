package com.vishwaagrotech.digitalhr.ui.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.model.Checklist
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
class TaskDetailViewModel
@Inject
constructor(private val repository: ProjectRepository) : ViewModel() {

    private val _taskDetail =
        MutableStateFlow<Task>(Task())
    val taskDetail = _taskDetail.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _toggleTask = MutableStateFlow(false)
    val toggleTask = _toggleTask.asStateFlow()

    fun getTaskDetailOverview(value: String) {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                repository.getTaskDetail(value)
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

                val attachments = ArrayList<Attachment>()
                for (attachment in value.attachments) {
                    attachments.add(Attachment(attachment.id, attachment.attachment_url,attachment.type))
                }

                val checklists = ArrayList<Checklist>()
                for (checklist in value.checklists) {
                    checklists.add(
                        Checklist(
                            checklist.id,
                            checklist.name,
                            checklist.task_id,
                            checklist.is_completed
                        )
                    )
                }

                val task = Task(
                    value.task_id,
                    value.task_name,
                    value.description,
                    value.start_date,
                    value.deadline,
                    value.project_name,
                    value.status,
                    value.priority,
                    teamList,
                    value.task_comments.size.toString(),
                    checklists,
                    attachments,
                    value.has_checklist
                )

                _taskDetail.value = task

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

    fun toggleCheckList(checklist: Checklist) {
        _loading.value = true
        viewModelScope.launch {
            val response = try {
                repository.toggleChecklist(checklist.id.toString())
            } catch (e: Exception) {
                e.printStackTrace();
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                if (response.body()!!.status) {
                    val checklist = _taskDetail.value.checklist.find {
                        it.id == checklist.id
                    }

                    if (checklist!!.status == "1") {
                        checklist.status = "0"
                    } else {
                        checklist.status = "1"
                    }

                    val checklistupdated = ArrayList(_taskDetail.value.checklist.toMutableList())

                    val task2 = _taskDetail.value.copy(
                        id = _taskDetail.value.id,
                        name = _taskDetail.value.name,
                        description = _taskDetail.value.description,
                        startDate = _taskDetail.value.startDate,
                        endDate = _taskDetail.value.endDate,
                        projectName = _taskDetail.value.projectName,
                        status = _taskDetail.value.status,
                        priority = _taskDetail.value.priority,
                        team = _taskDetail.value.team,
                        commentCount = _taskDetail.value.commentCount.toString(),
                        checklist = checklistupdated,
                        attachment = _taskDetail.value.attachment,
                        hasCheckList = _taskDetail.value.hasCheckList
                    )

                    _taskDetail.value = Task()
                    _taskDetail.value = task2

                }
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

    fun toggleTask() {
        _toggleTask.value = false
        _loading.value = true
        viewModelScope.launch {
            val response = try {
                repository.toggleTask(taskDetail.value.id.toString())
            } catch (e: Exception) {
                e.printStackTrace();
                _toggleTask.value = false
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                if (response.body()!!.status) {
                    _loading.value = false
                    _toggleTask.value = true
                }

            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loading.value = false
                _toggleTask.value = false
            }
        }
    }

}