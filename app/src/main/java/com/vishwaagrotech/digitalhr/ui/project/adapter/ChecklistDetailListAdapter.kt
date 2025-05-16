package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowCheckListBinding
import com.vishwaagrotech.digitalhr.handler.ChecklistHandler
import com.vishwaagrotech.digitalhr.model.Checklist

class ChecklistDetailListAdapter(
    private val data: ArrayList<Checklist>,
    private val checklistHandler: ChecklistHandler
) :
    RecyclerView.Adapter<ChecklistDetailListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChecklistDetailListAdapter.ViewHolder {
        val binding =
            RowCheckListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChecklistDetailListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowCheckListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(checklist: Checklist) {
            binding.checklist = checklist

            binding.root.setOnClickListener {
                checklistHandler.onChecklistClicked(checklist)
            }
        }
    }
}