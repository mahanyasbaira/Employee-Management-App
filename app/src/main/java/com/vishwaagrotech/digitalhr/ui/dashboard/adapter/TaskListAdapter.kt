package com.vishwaagrotech.digitalhr.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowTaskBinding
import com.vishwaagrotech.digitalhr.handler.TaskHandler
import com.vishwaagrotech.digitalhr.model.Task

class TaskListAdapter(
    private val data: ArrayList<Task>,
    private val taskHandler: TaskHandler
) :
    RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskListAdapter.ViewHolder {
        val binding =
            RowTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task

            binding.root.setOnClickListener {
                taskHandler.onTaskClicked(task)
            }
        }
    }
}