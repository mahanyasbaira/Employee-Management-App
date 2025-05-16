package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Checklist

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface ChecklistHandler {
    fun onChecklistClicked(checklist: Checklist)
}