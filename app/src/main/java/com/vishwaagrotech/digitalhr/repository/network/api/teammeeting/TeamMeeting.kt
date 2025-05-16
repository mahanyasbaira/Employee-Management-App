package com.vishwaagrotech.digitalhr.repository.network.api.teammeeting

data class TeamMeeting(
    val agenda: String,
    val id: Int,
    val image: String,
    val created_by: String,
    val meeting_date: String,
    val meeting_start_time: String,
    val participator: ArrayList<Participator>,
    val publish_date: String,
    val title: String,
    val venue: String
)