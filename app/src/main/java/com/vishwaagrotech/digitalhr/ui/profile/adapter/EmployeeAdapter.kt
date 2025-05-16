package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowEmployeeBinding

class EmployeeAdapter(
    private val data: ArrayList<com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee>,
    val employeeClicked: onEmployeeClicked
) :
    RecyclerView.Adapter<EmployeeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.ViewHolder {
        val binding =
            RowEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmployeeAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowEmployeeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee) {
            binding.employee = employee

            if (employee.online_status == 1) {
                binding.cardImage.setCardBackgroundColor(Color.parseColor("#2e7d32"))
            } else {
                binding.cardImage.setCardBackgroundColor(Color.parseColor("#ef5350"))
            }

            binding.root.setOnClickListener {
                employeeClicked.onProfileClicked(employee)
            }
        }
    }

    interface onEmployeeClicked {
        fun onCallClicked(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee)
        fun onMessageClicked(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee)
        fun onProfileClicked(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee)
    }

    fun calledClicked(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee) {
        employeeClicked.onCallClicked(employee)
    }

    fun messageClicked(employee: com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee) {
        employeeClicked.onMessageClicked(employee)
    }
}