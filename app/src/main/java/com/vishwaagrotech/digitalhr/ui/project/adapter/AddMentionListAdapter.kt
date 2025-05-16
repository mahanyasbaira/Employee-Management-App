package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowTeamListBinding
import com.vishwaagrotech.digitalhr.handler.MentionHandler
import com.vishwaagrotech.digitalhr.model.Team

class AddMentionListAdapter(
    private val data: ArrayList<Team>,
    private  val mentionHandler: MentionHandler
) :
    RecyclerView.Adapter<AddMentionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddMentionListAdapter.ViewHolder {
        val binding =
            RowTeamListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddMentionListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowTeamListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(team: Team) {
            binding.team = team

            binding.root.setOnClickListener {
                mentionHandler.onMentionAdded(team)
            }
        }
    }
}