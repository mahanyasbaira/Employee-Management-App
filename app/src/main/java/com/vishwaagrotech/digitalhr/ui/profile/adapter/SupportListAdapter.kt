package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.*
import com.vishwaagrotech.digitalhr.handler.SupportHandler
import com.vishwaagrotech.digitalhr.model.Support

class SupportListAdapter(
    private val data: ArrayList<Support>,
    private val supportHandler: SupportHandler
) :
    RecyclerView.Adapter<SupportListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportListAdapter.ViewHolder {
        val binding =
            RowSupportListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        binding.executePendingBindings()
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupportListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowSupportListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(support: Support) {
            binding.support = support

            binding.root.setOnClickListener {
                supportHandler.onSupportClicked(support)
            }

            if(support.status == "Pending"){
                binding.textStatus.setTextColor(binding.root.context.getColor(R.color.dark_orange))
            }else if(support.status == "In Progress"){
                binding.textStatus.setTextColor(binding.root.context.getColor(R.color.red_end))
            }else{
                binding.textStatus.setTextColor(binding.root.context.getColor(R.color.green_end))
            }
        }
    }
}