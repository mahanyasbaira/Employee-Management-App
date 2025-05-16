package com.vishwaagrotech.digitalhr.ui.tada.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowAttachmentLocalBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment

class LocalAttachmentListAdapter(
    private val data: ArrayList<Attachment>,
    private val attachmentHandler: AttachmentHandler
) :
    RecyclerView.Adapter<LocalAttachmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalAttachmentListAdapter.ViewHolder {
        val binding =
            RowAttachmentLocalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocalAttachmentListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowAttachmentLocalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.attachment = attachment
        }
    }

    fun onDeleteClicked(attachment: Attachment) {
        attachmentHandler.onAttachmentClicked(attachment)
    }
}