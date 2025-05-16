package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.User
import com.vishwaagrotech.digitalhr.repository.HeaderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeaderViewModel
@Inject
constructor(
    private val repository: HeaderRepository
) : ViewModel() {

    private val _person = MutableLiveData<User>()

    val person: LiveData<User>
        get() = _person


    fun getUserDetail() {
        viewModelScope.launch {
            val user = repository.getUserDetail()

            _person.value = user
        }
    }

    init {
        getUserDetail()
    }
}