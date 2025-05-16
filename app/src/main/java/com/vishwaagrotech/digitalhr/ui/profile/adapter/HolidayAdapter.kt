package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowHolidayBinding
import com.vishwaagrotech.digitalhr.model.Holiday

class HolidayAdapter(
    private val data: ArrayList<Holiday>
) :
    RecyclerView.Adapter<HolidayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayAdapter.ViewHolder {
        val binding =
            RowHolidayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HolidayAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowHolidayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(holiday: Holiday) {
            binding.holiday = holiday
        }
    }
}