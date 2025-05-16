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
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.RowReplyBinding
import com.vishwaagrotech.digitalhr.handler.CommentHandler
import com.vishwaagrotech.digitalhr.model.Reply

class ReplyListAdapter(
    private val data: ArrayList<Reply>,
    private val userId : Int,
    private val commentHandler: CommentHandler
) :
    RecyclerView.Adapter<ReplyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReplyListAdapter.ViewHolder {
        val binding =
            RowReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reply: Reply) {
            binding.reply = reply

            if(reply.created_by_id.toInt() == userId){
                binding.textDelete.visibility = View.VISIBLE

                binding.textDelete.setOnClickListener {
                    commentHandler.onReplyDeleteClicked(reply)
                }
            }else{
                binding.textDelete.visibility = View.GONE
            }

            var desc = ""
            for(mention in reply.mentioned){
                desc += "@"+mention.username + " "
            }

            desc += "  "+reply.description



            val spannableStringBuilder = SpannableStringBuilder(desc)
            for(mention in reply.mentioned){
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
    }
}