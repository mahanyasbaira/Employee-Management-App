package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.RowCommentBinding
import com.vishwaagrotech.digitalhr.handler.CommentHandler
import com.vishwaagrotech.digitalhr.model.Comment
import com.vishwaagrotech.digitalhr.model.Reply
import com.vishwaagrotech.digitalhr.ui.project.CommentListFragment

class CommentListAdapter(
    private val data: ArrayList<Comment>,
    private val userId: Int,
    private val commentHandler: CommentHandler,
    private val replyCallBack: ReplyCallBack
) :
    RecyclerView.Adapter<CommentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentListAdapter.ViewHolder {
        val binding =
            RowCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowCommentBinding) :
        RecyclerView.ViewHolder(binding.root), CommentHandler {
        fun bind(comment: Comment) {
            binding.comment = comment

            val commentListAdapter = ReplyListAdapter(comment.replies, userId, this);
            binding.recyclerViewReply.apply {
                adapter = commentListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            if (comment.created_by_id.toInt() == userId) {
                binding.textDelete.visibility = View.VISIBLE

                binding.textDelete.setOnClickListener {
                    commentHandler.onCommentDeleteClicked(comment)
                }
            } else {
                binding.textDelete.visibility = View.GONE
            }

            binding.textReply.setOnClickListener {
                commentHandler.onReplyCommentClicked(comment)
            }

            if(CommentListFragment.commentId == comment.id.toString()){
                binding.textReply.text = "Cancel Reply"
            }else{
                binding.textReply.text = "Reply"
            }

            var desc = ""
            for(mention in comment.mentioned){
                desc += "@"+mention.username + " "
            }

            desc += "  "+comment.description

            val spannableStringBuilder = SpannableStringBuilder(desc)
            for(mention in comment.mentioned){
                var person = "@"+mention.username

                val startIndex = desc.indexOf(person)

                val endIndex = startIndex + person.length

                val click1Span = object : ClickableSpan() {
                    override fun onClick(view: View) {
                        val bundle = Bundle()
                        bundle.putString("userId", mention.id.toString())
                        Navigation.findNavController(view!!)
                            .navigate(R.id.action_commentListFragment_to_employeeDetailFragment2, bundle)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        // Remove underline
                        ds.isUnderlineText = false
                    }
                }
                val underlineSpans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length, UnderlineSpan::class.java)
                for (underlineSpan in underlineSpans) {
                    spannableStringBuilder.removeSpan(underlineSpan)
                }
                spannableStringBuilder.setSpan(click1Span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(ForegroundColorSpan(Color.WHITE), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            }
            binding.textDescription.movementMethod = LinkMovementMethod.getInstance()
            binding.textDescription.text = spannableStringBuilder

        }

        override fun onCommentDeleteClicked(comment: Comment) {

        }

        override fun onReplyDeleteClicked(reply: Reply) {
            replyCallBack.onReplyDeleteCallBack(reply)
        }

        override fun onReplyCommentClicked(comment: Comment) {
        }
    }

    interface ReplyCallBack {
        fun onReplyDeleteCallBack(reply: Reply)
    }
}