package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.Leave
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist.LeaveRequestList

object LeaveListMapper {
    fun mapToEntity(leave: LeaveRequestList): Leave {
        return Leave(
            leave.id,
            leave.leave_type_name,
            leave.admin_remark,
            leave.leave_from,
            leave.leave_to,
            if (leave.status.contentEquals("Rejected")) {
                2
            } else if (leave.status.contentEquals("Approved")) {
                1
            } else {
                0
            },
            leave.leave_requested_date,
            leave.status_updated_by
        )
    }

    fun mapToEntityList(entities: ArrayList<LeaveRequestList>): List<Leave> {
        return entities.map {
            mapToEntity(it)
        }
    }
}