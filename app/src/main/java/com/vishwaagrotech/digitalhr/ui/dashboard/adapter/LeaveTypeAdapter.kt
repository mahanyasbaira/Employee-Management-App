package com.vishwaagrotech.digitalhr.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowLeaveTypeBinding
import com.vishwaagrotech.digitalhr.model.LeaveType

class LeaveTypeAdapter(
    private val data: ArrayList<LeaveType>
) :
    RecyclerView.Adapter<LeaveTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveTypeAdapter.ViewHolder {
        val binding =
            RowLeaveTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveTypeAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowLeaveTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(leaveType: LeaveType) {
            binding.leaveType = leaveType
        }
    }
}