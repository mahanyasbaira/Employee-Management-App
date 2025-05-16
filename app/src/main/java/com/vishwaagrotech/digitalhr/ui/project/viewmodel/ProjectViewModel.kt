package com.vishwaagrotech.digitalhr.ui.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Project
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
class ProjectViewModel
@Inject
constructor(private val repository: ProjectRepository) : ViewModel() {

    private val _projectList =
        MutableStateFlow<ArrayList<Project>>(arrayListOf())
    val projectList = _projectList.asStateFlow()

    val projectFilterList = ArrayList<Project>()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _selected =
        MutableStateFlow<String>("All")
    val selected = _selected.asStateFlow()

    fun getTaskListOverview() {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                repository.getProjectList()
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                val value = response.body()!!.data

                val projects = Project.parseDataList(value)
                projectFilterList.clear()
                projectFilterList.addAll(projects)

                makeList()
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

    fun onFilterSelected(value: String) {
        _selected.value = value
        makeList()
    }

    fun makeList() {
        if (selected.value == "All") {
            _projectList.value = projectFilterList
        } else {
            val value = projectFilterList.filter {
                it.status == selected.value
            }.toMutableList()
            val projects = ArrayList<Project>()
            projects.addAll(value)
            _projectList.value = projects
        }

    }

}