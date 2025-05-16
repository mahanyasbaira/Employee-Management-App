package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Task

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface TaskHandler {
    fun onTaskClicked(task: Task)
}