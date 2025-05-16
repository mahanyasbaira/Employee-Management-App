package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.Participant
import com.vishwaagrotech.digitalhr.repository.network.api.teammeeting.Participator
import kotlin.collections.ArrayList

object ParticipantMapper {

    fun mapToEntity(participator: Participator): Participant {

        return Participant(
            participator.avatar,
            participator.email,
            participator.id,
            participator.name,
            participator.online_status,
            participator.phone,
            participator.post
        )
    }

    fun mapToEntityList(entities: ArrayList<Participator>): List<Participant> {
        return entities.map {
            mapToEntity(it)
        }
    }
}