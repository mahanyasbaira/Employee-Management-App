package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.Meeting
import com.vishwaagrotech.digitalhr.model.Participant
import com.vishwaagrotech.digitalhr.repository.network.api.teammeeting.TeamMeeting
import kotlin.collections.ArrayList

object TeamMeetingMapper {

    fun mapToEntity(meeting: TeamMeeting): Meeting {

        return Meeting(
            meeting.agenda,
            meeting.id,
            meeting.image,
            meeting.created_by,
            meeting.meeting_date,
            meeting.meeting_start_time,
            ParticipantMapper.mapToEntityList(meeting.participator) as ArrayList<Participant>,
            meeting.publish_date,
            meeting.title,
            meeting.venue
        )
    }

    fun mapToEntityList(entities: ArrayList<TeamMeeting>): List<Meeting> {
        return entities.map {
            mapToEntity(it)
        }
    }
}