package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowLeaveListBinding
import com.vishwaagrotech.digitalhr.model.Leave

class LeaveAdapter(
    private val data: ArrayList<Leave>
) :
    RecyclerView.Adapter<LeaveAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveAdapter.ViewHolder {
        val binding =
            RowLeaveListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowLeaveListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(leave: Leave) {
            binding.leave = leave

            if (leave.status == 2) {
                //red
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#e87a6b"))
                binding.textStatus.text = "Rejected"
            } else if (leave.status == 1) {
                //green
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#00ab6b"))
                binding.textStatus.text = "Approved"
            } else {
                //orange
                binding.cardStatus.setCardBackgroundColor(Color.parseColor("#ffa159"))
                binding.textStatus.text = "Pending"
            }
        }
    }
}