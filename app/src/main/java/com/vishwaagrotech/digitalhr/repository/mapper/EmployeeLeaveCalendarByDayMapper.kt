package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.LeaveEventEmployee
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendarbyday.EmployeeLeaveCalendarByDay
import java.util.*

object EmployeeLeaveCalendarByDayMapper {

    fun mapToEntity(leave: EmployeeLeaveCalendarByDay): LeaveEventEmployee {
        return LeaveEventEmployee(
            leave.leave_id,
            leave.user_avatar,
            leave.user_name,
            leave.post,
            leave.leave_days,
            leave.leave_status
        )
    }

    fun mapToEntityList(entities: ArrayList<EmployeeLeaveCalendarByDay>): List<LeaveEventEmployee> {
        return entities.map {
            mapToEntity(it)
        }
    }
}