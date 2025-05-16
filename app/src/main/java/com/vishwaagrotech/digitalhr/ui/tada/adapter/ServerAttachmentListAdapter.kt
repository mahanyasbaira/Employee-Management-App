package com.vishwaagrotech.digitalhr.ui.tada.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowAttachmentServerBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment

class ServerAttachmentListAdapter(
    private val data: ArrayList<Attachment>,
    private val attachmentHandler: AttachmentHandler
) :
    RecyclerView.Adapter<ServerAttachmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerAttachmentListAdapter.ViewHolder {
        val binding =
            RowAttachmentServerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServerAttachmentListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowAttachmentServerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.attachment = attachment

            binding.root.setOnClickListener {
                onTadaLoadClicked(attachment)
            }
        }
    }

    fun onDeleteClicked(attachment: Attachment) {
        attachmentHandler.onRemoveAttachmentClicked(attachment)
    }

    fun onTadaLoadClicked(attachment: Attachment) {
        attachmentHandler.onLoadAttachmentClicked(attachment)
    }
}