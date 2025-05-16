package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.Attendance
import com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport.EmployeeAttendance

object AttendanceMapper {

    fun mapToEntity(attendance: EmployeeAttendance): Attendance {
        return Attendance(
            attendance.id,
            attendance.attendance_date,
            attendance.week_day,
            attendance.check_in,
            attendance.check_out
        )
    }

    fun mapToEntityList(entities: ArrayList<EmployeeAttendance>): List<Attendance> {
        return entities.map {
            mapToEntity(it)
        }
    }
}