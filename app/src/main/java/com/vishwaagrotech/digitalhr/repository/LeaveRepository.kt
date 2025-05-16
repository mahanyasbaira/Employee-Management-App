package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LeaveRepository
@Inject
constructor(
    val retrofitService: RetrofitService,
    val dataStoreManager: DataStoreManager
) {
    suspend fun getLeaveType() = retrofitService.getLeaveType(" Bearer ".plus(getToken()))

    suspend fun getLeaveRequest(
        leave_from: String,
        leave_to: String,
        leave_type_id: Int,
        reasons: String,
        early_exit: Int
    ) = retrofitService.getLeaveRequestResponse(
        " Bearer ".plus(getToken()),
        leave_from,
        leave_to,
        leave_type_id,
        reasons,
        early_exit
    )

    suspend fun getLeaveRequestList(leave_type: Int, month: Int) =
        retrofitService.getLeaveRequestListResponse(
            "Bearer ${getToken()}",
            if (leave_type == 0) {
                ""
            } else {
                leave_type.toString()
            },
            if (month == 0) {
                ""
            } else {
                month.toString()
            }
        )

    suspend fun getToken() = dataStoreManager.getToken().first()

    suspend fun getActiveStatus() = dataStoreManager.getActiveStatus().first()
}