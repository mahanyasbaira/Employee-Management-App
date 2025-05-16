package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getCommentList(taskId: String, per_page: Int, page: Int) =
        retrofitService.getCommentList(
            "Bearer ${getToken()}",
            taskId.toString(),
            per_page.toString(),
            page.toString()
        )

    suspend fun deleteComment(id: String) =
        retrofitService.deleteComment(
            "Bearer ${getToken()}",
            id.toString(),
        )

    suspend fun deleteReply(id: String) =
        retrofitService.deleteReply(
            "Bearer ${getToken()}",
            id.toString(),
        )

    suspend fun saveComment(
        taskId: String,
        commentId: String,
        description: String,
        mentioned: ArrayList<String>
    ) =
        if (mentioned.isEmpty()) retrofitService.saveCommentWithoutMention(
            "Bearer ${getToken()}",
            taskId,
            commentId, description
        ) else retrofitService.saveComment(
            "Bearer ${getToken()}",
            taskId,
            commentId, description, mentioned
        )

    suspend fun getToken() = dataStoreManager.getToken().first()
}