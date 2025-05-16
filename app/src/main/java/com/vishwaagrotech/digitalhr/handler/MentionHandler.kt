package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Mentioned
import com.vishwaagrotech.digitalhr.model.Team

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface MentionHandler {
    fun onMentionRemoved(mentioned: Mentioned)
    fun onMentionAdded(team: Team)
}