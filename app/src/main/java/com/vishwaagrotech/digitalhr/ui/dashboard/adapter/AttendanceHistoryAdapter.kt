package com.vishwaagrotech.digitalhr.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowAttendanceBinding
import com.vishwaagrotech.digitalhr.model.Attendance

class AttendanceHistoryAdapter(
    private val data: ArrayList<Attendance>
) :
    RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AttendanceHistoryAdapter.ViewHolder {
        val binding =
            RowAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceHistoryAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            binding.attendance = attendance
        }
    }
}