package com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.vishwaagrotech.digitalhr.model.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel  @Inject constructor() : ViewModel() {

    fun getNotice() = dataNotice

    private var dataNotice = arrayListOf(
        Notice(
            1,
            "Holiday Request",
            "This is to inform all that sunday the office will be remaining closed in the occation of Local election 2079",
            "June",
            "1"
        ),
        Notice(
            2,
            "Holiday Request",
            "This is to inform all that sunday the office will be remaining closed in the occation of Local election 2079",
            "June",
            "1"
        ),
        Notice(
            3,
            "Holiday Request",
            "This is to inform all that sunday the office will be remaining closed in the occation of Local election 2079",
            "June",
            "11"
        ),
        Notice(
            4,
            "Holiday Request",
            "This is to inform all that sunday the office will be remaining closed in the occation of Local election 2079",
            "June",
            "21"
        ),
        Notice(
            5,
            "Holiday Request",
            "This is to inform all that sunday the office will be remaining closed in the occation of Local election 2079",
            "June",
            "31"
        ),
    )
}