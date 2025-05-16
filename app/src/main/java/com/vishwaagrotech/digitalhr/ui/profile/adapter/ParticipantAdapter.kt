package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowParticipantBinding
import com.vishwaagrotech.digitalhr.model.Participant

class ParticipantAdapter(
    private val data: ArrayList<Participant>
) :
    RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ParticipantAdapter.ViewHolder {
        val binding =
            RowParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        binding.executePendingBindings()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParticipantAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        if (data.size > 3) {
            return 3
        } else {
            return data.size
        }
    }

    inner class ViewHolder(val binding: RowParticipantBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(participant: Participant) {
            binding.participant = participant
        }
    }
}