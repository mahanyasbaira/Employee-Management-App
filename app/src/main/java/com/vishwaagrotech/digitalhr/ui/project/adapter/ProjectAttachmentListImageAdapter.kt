package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowAttachmentProjectTaskImageBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment

class ProjectAttachmentListImageAdapter(
    private val data: ArrayList<Attachment>,
    private val attachmentHandler: AttachmentHandler
) :
    RecyclerView.Adapter<ProjectAttachmentListImageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAttachmentListImageAdapter.ViewHolder {
        val binding =
            RowAttachmentProjectTaskImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProjectAttachmentListImageAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowAttachmentProjectTaskImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attachment: Attachment) {
            binding.attachment = attachment

            binding.root.setOnClickListener {
                attachmentHandler.onLoadAttachmentClicked(attachment)
            }
        }
    }
}