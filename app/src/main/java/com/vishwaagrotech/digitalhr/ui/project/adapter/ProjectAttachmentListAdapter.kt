package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowAttachmentProjectTaskBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment

class ProjectAttachmentListAdapter(
    private val data: ArrayList<Attachment>,
    private val attachmentHandler: AttachmentHandler
) :
    RecyclerView.Adapter<ProjectAttachmentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAttachmentListAdapter.ViewHolder {
        val binding =
            RowAttachmentProjectTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectAttachmentListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowAttachmentProjectTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.attachment = attachment

            binding.root.setOnClickListener {
                attachmentHandler.onLoadAttachmentClicked(attachment)
            }
        }
    }
}