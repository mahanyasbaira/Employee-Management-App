package com.vishwaagrotech.digitalhr.ui.profile.viewmodel

import android.text.format.DateFormat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Department
import com.vishwaagrotech.digitalhr.model.Support
import com.vishwaagrotech.digitalhr.repository.SupportRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SupportViewModel
@Inject
constructor(val repository: SupportRepository) : ViewModel() {

    private var _rDepartments =
        MutableStateFlow<EventHandler<ArrayList<Department>>>(EventHandler.Empty)

    var rDepartment = _rDepartments.asStateFlow()

    private var _rSaveSupport =
        MutableStateFlow<EventHandler<Boolean>>(EventHandler.Empty)

    var rSaveSupport = _rSaveSupport.asStateFlow();


    var departmentId = 0

    private var _rSupportList =
        MutableStateFlow<EventHandler<ArrayList<Support>>>(EventHandler.Empty)

    var supportList = _rSupportList.asStateFlow()

    fun getSupportDepartments() {
        viewModelScope.launch {
            val response = try {
                repository.getSupportDepartmentsResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            if (response.isSuccessful) {
                val departments: ArrayList<Department> = ArrayList()
                for (department in response.body()!!.data) {
                    departments.add(Department(department.id, department.dept_name))
                }

                _rDepartments.value = EventHandler.Success(departments)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _rDepartments.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun saveSupport(title: String, description: String) {
        _rSaveSupport.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.saveSupportResponse(title, description, departmentId)
            } catch (e: Exception) {
                e.printStackTrace()
                _rSaveSupport.value = EventHandler.Failure("Something went wrong")
                return@launch
            }

            if (response.isSuccessful) {
                _rSaveSupport.value = EventHandler.Success(true)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _rSaveSupport.value = EventHandler.Failure(responseError.message)
            }
        }
    }

    fun getSupportList() {
        _rSupportList.value = EventHandler.Loading
        viewModelScope.launch {
            val response = try {
                repository.getSupportListResponse()
            } catch (e: Exception) {
                e.printStackTrace()
                _rSupportList.value = EventHandler.Failure("Something went wrong.")
                return@launch
            }

            if (response.isSuccessful) {
                val supports: ArrayList<Support> = ArrayList()
                for (support in response.body()!!.data.data) {

                    val fullDate = support.query_date
                    val format = SimpleDateFormat("MMMM dd yyyy", Locale.US)
                    val date = format.parse(fullDate)

                    val day = DateFormat.format("dd", date) as String
                    val monthNumber = DateFormat.format("MMM", date) as String

                    supports.add(
                        Support(
                            0,
                            support.title,
                            support.description,
                            support.requested_department,
                            support.status,
                            support.updated_by,
                            support.updated_at,
                            support.query_date,
                            day,monthNumber
                        )
                    )
                }

                _rSupportList.value = EventHandler.Success(supports)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _rDepartments.value = EventHandler.Failure(responseError.message)
            }
        }
    }

}