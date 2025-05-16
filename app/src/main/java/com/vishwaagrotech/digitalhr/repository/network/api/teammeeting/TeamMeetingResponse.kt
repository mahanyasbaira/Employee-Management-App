package com.vishwaagrotech.digitalhr.repository.network.api.teammeeting

data class TeamMeetingResponse(
    val `data`: List<TeamMeeting>,
    val status: Boolean,
    val status_code: Int
)