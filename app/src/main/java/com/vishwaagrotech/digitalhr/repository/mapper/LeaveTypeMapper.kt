package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.LeaveType
import com.vishwaagrotech.digitalhr.repository.network.api.leavetype.LeaveTypeDomain

object LeaveTypeMapper {

    fun mapToEntity(leave: LeaveTypeDomain): LeaveType {
        return LeaveType(
            leave.leave_type_id,
            leave.leave_type_name,
            leave.total_leave_allocated,
            leave.leave_taken,
            leave.early_exit,
            leave.leave_type_status
        )
    }

    fun mapToEntityList(entities: ArrayList<LeaveTypeDomain>): List<LeaveType> {
        return entities.map {
            mapToEntity(it)
        }
    }
}