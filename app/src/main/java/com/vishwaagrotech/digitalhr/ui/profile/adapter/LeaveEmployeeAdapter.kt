package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowLeaveEmployeeBinding
import com.vishwaagrotech.digitalhr.model.LeaveEventEmployee

class LeaveEmployeeAdapter(
    private val data: ArrayList<LeaveEventEmployee>
) :
    RecyclerView.Adapter<LeaveEmployeeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaveEmployeeAdapter.ViewHolder {
        val binding =
            RowLeaveEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveEmployeeAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowLeaveEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(leaveEventEmployee: LeaveEventEmployee) {
            binding.leaveEmployee = leaveEventEmployee

            if (leaveEventEmployee.status.contentEquals("Rejected")) {
                //red
                binding.cardDays.setCardBackgroundColor(Color.parseColor("#e87a6b"))
            } else if (leaveEventEmployee.status.contentEquals("Approved")) {
                //green
                binding.cardDays.setCardBackgroundColor(Color.parseColor("#00ab6b"))
            } else {
                //orange
                binding.cardDays.setCardBackgroundColor(Color.parseColor("#ffa159"))
            }
        }
    }
}