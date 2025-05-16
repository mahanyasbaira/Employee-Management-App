package com.vishwaagrotech.digitalhr.ui.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Comment
import com.vishwaagrotech.digitalhr.model.Mentioned
import com.vishwaagrotech.digitalhr.model.Reply
import com.vishwaagrotech.digitalhr.repository.CommentRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class CommentViewModel
@Inject
constructor(private val repository: CommentRepository) : ViewModel() {

    private val _commentList = MutableStateFlow<ArrayList<Comment>>(arrayListOf())
    val commentList = _commentList.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _loadingComment = MutableStateFlow(false)
    val loadingComment = _loadingComment.asStateFlow()

    val per_page = 20;
    var page = 1;

    var taskId = "0"
    var commentId = ""
    var description = ""
    var mentioned = ArrayList<Mentioned>()

    fun getComments(taskId: String) {
        viewModelScope.launch {
            _loading.value = true
            _loadingComment.value = true
            val response = try {
                repository.getCommentList(taskId, per_page, page)
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                _loadingComment.value = false
                return@launch
            }

            if (response.isSuccessful) {
                val value = response.body()!!.data

                var commentList = ArrayList<Comment>()
                for (comment in value) {

                    var mentionedList = ArrayList<Mentioned>()
                    var replyList = ArrayList<Reply>()

                    for (mention in comment.mentioned) {
                        mentionedList.add(Mentioned(mention.id, mention.name, mention.username))
                    }

                    for (reply in comment.replies) {
                        var replymentionedList = ArrayList<Mentioned>()

                        for (mention in reply.mentioned) {
                            replymentionedList.add(
                                Mentioned(
                                    mention.id,
                                    mention.name,
                                    mention.username
                                )
                            )
                        }

                        replyList.add(
                            Reply(
                                reply.avatar,
                                reply.comment_id,
                                reply.created_at,
                                reply.created_by_id,
                                reply.created_by_name,
                                reply.description,
                                replymentionedList, reply.reply_id, reply.username
                            )
                        )
                    }

                    commentList.add(
                        Comment(
                            comment.avatar,
                            comment.created_at,
                            comment.created_by_id,
                            comment.created_by_name,
                            comment.description,
                            comment.id,
                            mentionedList,
                            replyList,
                            comment.username
                        )
                    )
                }

                if (commentList.isNotEmpty()){
                    page++
                }

                var list = _commentList.value.toMutableList()
                list.addAll(commentList)
                _commentList.value = ArrayList(list)

                _loading.value = false
                _loadingComment.value = false
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loading.value = false
                _loadingComment.value = false
            }
        }
    }

    fun deleteReply(reply: Reply) {
        _loading.value = true
        viewModelScope.launch {
            val response = try {
                repository.deleteReply(reply.reply_id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {
                val updatedComment = _commentList.value.toMutableList()

                val tempComment = updatedComment.find {
                    it.id == reply.comment_id.toInt()
                }

                if (tempComment != null) {
                    val tempReply = tempComment.replies.find {
                        it.reply_id == reply.reply_id
                    }

                    if (tempReply != null) {
                        tempComment.replies.remove(tempReply)
                    }
                }

                _commentList.value = ArrayList(updatedComment)

                _loading.value = false
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loading.value = false
            }
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch {
            _loading.value = true
            val response = try {
                repository.deleteComment(comment.id.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {

                if (response.body()!!.status) {
                    val tempComments = _commentList.value.toMutableList()
                    val tempComment = tempComments.find {
                        it.id == comment.id
                    }

                    tempComments.remove(tempComment)

                    _commentList.value = ArrayList(tempComments)
                }

                _loading.value = false
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )
                _loading.value = false
            }
        }
    }

    fun sendComment(
        tId: String,
        cId: String,
        rDescription: String
    ) {
        taskId = tId
        commentId = cId
        description = rDescription

        saveComment()
    }

    fun saveComment(
    ) {
        _loading.value = true
        viewModelScope.launch {
            val response = try {
                val mentions = ArrayList<String>()
                for (mention in mentioned) {
                    mentions.add(mention.id.toString())
                }
                repository.saveComment(taskId, commentId, description, mentions)
            } catch (e: Exception) {
                e.printStackTrace()
                _loading.value = false
                return@launch
            }

            if (response.isSuccessful) {

                if (response.body()!!.status) {
                    val value = response.body()!!.data

                    var mentionedList = ArrayList<Mentioned>()
                    var replyList = ArrayList<Reply>()

                    for (mention in value.mentioned) {
                        mentionedList.add(Mentioned(mention.id, mention.name, mention.username))
                    }

                    for (reply in value.replies) {
                        var replymentionedList = ArrayList<Mentioned>()

                        for (mention in reply.mentioned) {
                            replymentionedList.add(
                                Mentioned(
                                    mention.id,
                                    mention.name,
                                    mention.username
                                )
                            )
                        }

                        replyList.add(
                            Reply(
                                reply.avatar,
                                reply.comment_id,
                                reply.created_at,
                                reply.created_by_id,
                                reply.created_by_name,
                                reply.description,
                                replymentionedList, reply.reply_id, reply.username
                            )
                        )
                    }

                    val newComment = Comment(
                        value.avatar,
                        value.created_at,
                        value.created_by_id,
                        value.created_by_name,
                        value.description,
                        value.id,
                        mentionedList,
                        replyList,
                        value.username
                    )
                    var tempComments = _commentList.value.toMutableList()


                    if (commentId == "") {
                        tempComments.add(0, newComment)
                    } else {
                        var value = tempComments.find {
                            it.id.toString() == commentId
                        }

                        value!!.replies = replyList
                    }

                    _commentList.value = ArrayList(tempComments)

                    commentId = ""
                    _loading.value = false
                }

            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _loading.value = false
            }
        }
    }

    init {
        page = 1
        taskId = "0"
        commentId = ""
        description = ""
        mentioned = ArrayList()
    }

}