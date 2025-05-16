package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.*
import com.vishwaagrotech.digitalhr.handler.MeetingHandler
import com.vishwaagrotech.digitalhr.model.Participant

class ParticipantDetailAdapter(
    private val data: ArrayList<Participant>,
    private val meetingHandler: MeetingHandler
) :
    RecyclerView.Adapter<ParticipantDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantDetailAdapter.ViewHolder {
        val binding =
            RowParticipantDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        binding.executePendingBindings()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantDetailAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowParticipantDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participant: Participant) {
            binding.participant = participant

            binding.root.setOnClickListener {
                meetingHandler.onProfileClicked(participant)
            }
        }
    }
}