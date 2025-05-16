package com.vishwaagrotech.digitalhr.handler

import com.vishwaagrotech.digitalhr.model.Comment
import com.vishwaagrotech.digitalhr.model.Reply

/**
 *Copyright (c) 2023, All Rights Reserved.
 */
interface CommentHandler {
    fun onCommentDeleteClicked(comment: Comment)
    fun onReplyDeleteClicked(reply: Reply)
    fun onReplyCommentClicked(comment: Comment)
}