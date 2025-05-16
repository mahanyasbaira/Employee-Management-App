package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Meeting
import com.vishwaagrotech.digitalhr.model.Participant

/**
 *Copyright (c) 2022, All Rights Reserved.
 */
interface MeetingHandler {
    fun onMeetingClicked(meeting: Meeting)
    fun onProfileClicked(participant: Participant)
}