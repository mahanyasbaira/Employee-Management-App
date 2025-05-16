package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Tada

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface TadaHandler {
    fun onTadaClicked(tada: Tada)
    fun onEditTadaClicked(tada: Tada)
}