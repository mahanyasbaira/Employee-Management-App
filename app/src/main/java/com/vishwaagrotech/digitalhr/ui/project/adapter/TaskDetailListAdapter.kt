package com.vishwaagrotech.digitalhr.ui.project.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowTaskListBinding
import com.vishwaagrotech.digitalhr.handler.TaskHandler
import com.vishwaagrotech.digitalhr.model.Task

class TaskDetailListAdapter(
    private val data: ArrayList<Task>,
    private val taskHandler: TaskHandler
) :
    RecyclerView.Adapter<TaskDetailListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TaskDetailListAdapter.ViewHolder {
        val binding =
            RowTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskDetailListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowTaskListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task

            binding.root.setOnClickListener {
                taskHandler.onTaskClicked(task)
            }
        }
    }
}