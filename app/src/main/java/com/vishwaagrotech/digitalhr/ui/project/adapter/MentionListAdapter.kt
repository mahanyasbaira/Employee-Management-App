package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowMentionBinding
import com.vishwaagrotech.digitalhr.handler.MentionHandler
import com.vishwaagrotech.digitalhr.model.Mentioned

class MentionListAdapter(
    private val data: ArrayList<Mentioned>,
    private val mentionHandler: MentionHandler,
) :
    RecyclerView.Adapter<MentionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MentionListAdapter.ViewHolder {
        val binding =
            RowMentionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MentionListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowMentionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mentioned: Mentioned) {
            binding.mention = mentioned

            binding.imageAdd.setOnClickListener {
                mentionHandler.onMentionRemoved(mentioned)
            }
        }
    }
}