package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Project
import com.vishwaagrotech.digitalhr.model.Task
import com.vishwaagrotech.digitalhr.repository.ProjectRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.repository.network.api.projectdashboardresponse.Progress
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProjectViewModel
@Inject
constructor(private val repository: ProjectRepository) : ViewModel() {
    private val task = 6;
    private val project = 6;

    val _projectOverview =
        MutableLiveData(ProjectOverview(0, 0, 0))

    private val _projectList =
        MutableStateFlow<ArrayList<Project>>(arrayListOf())
    val projectList = _projectList.asStateFlow()

    private val _taskList = MutableStateFlow<ArrayList<Task>>(arrayListOf())
    val taskList = _taskList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun getProjectOverview() {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                repository.getProjectOverview(task, project)
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                val value = response.body()!!.data
                //parse project summary
                val projectSummary = ProjectOverview.parseData(value.progress)
                _projectOverview.postValue(projectSummary)

                //parse projects
                val projects = Project.parseData(value.projects)
                _projectList.value = projects

                //parse task
                val tasks = Task.parseData(value.assigned_task)
                _taskList.value = tasks

                Log.e("TaskListValue",_taskList.value.toString())

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

    data class ProjectOverview(
        val totalTask: Int,
        val totalCompleted: Int,
        val totalProjectProgress: Int,
    ) {
        companion object {
            fun parseData(progress: Progress): ProjectOverview {
                return ProjectOverview(
                    progress.total_task_assigned,
                    progress.total_task_completed,
                    progress.progress_in_percent
                )
            }
        }
    }

}