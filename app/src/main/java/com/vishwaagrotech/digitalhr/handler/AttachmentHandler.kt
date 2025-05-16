package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Attachment

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface AttachmentHandler {
    fun onAttachmentClicked(attachment: Attachment)
    fun onRemoveAttachmentClicked(attachment: Attachment)
    fun onLoadAttachmentClicked(attachment: Attachment)
}