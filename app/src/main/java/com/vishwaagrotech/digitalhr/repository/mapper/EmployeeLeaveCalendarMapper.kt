package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.LeaveEvent
import com.vishwaagrotech.digitalhr.repository.network.api.employeeleavecalendar.EmployeeLeaveCalendar
import com.vishwaagrotech.digitalhr.utils.Constant.formatter
import java.util.*

object EmployeeLeaveCalendarMapper {

    fun mapToEntity(leave: EmployeeLeaveCalendar): LeaveEvent {
        return LeaveEvent(
            leave.leave_count,
            formatter.parse(leave.date)!!
        )
    }

    fun mapToEntityList(entities: ArrayList<EmployeeLeaveCalendar>): List<LeaveEvent> {
        return entities.map {
            mapToEntity(it)
        }
    }
}