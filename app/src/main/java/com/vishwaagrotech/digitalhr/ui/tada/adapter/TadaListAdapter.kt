package com.vishwaagrotech.digitalhr.ui.tada.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.RowTadaListBinding
import com.vishwaagrotech.digitalhr.handler.TadaHandler
import com.vishwaagrotech.digitalhr.model.Tada

class TadaListAdapter(
    private val data: ArrayList<Tada>,
    private val tadaHandler: TadaHandler
) :
    RecyclerView.Adapter<TadaListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TadaListAdapter.ViewHolder {
        val binding =
            RowTadaListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TadaListAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowTadaListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tada: Tada) {
            binding.tada = tada

            if(tada.status == "Accepted"){
                binding.textStatus.text = "A"
                binding.cardStatus.setCardBackgroundColor(binding.root.context.getColor(R.color.green_start))
            }else if(tada.status == "Pending"){
                binding.textStatus.text = "P"
                binding.cardStatus.setCardBackgroundColor(binding.root.context.getColor(R.color.dark_orange))
            }else{
                binding.textStatus.text = "R"
                binding.cardStatus.setCardBackgroundColor(binding.root.context.getColor(R.color.red_end))
            }

            binding.root.setOnClickListener {
                onTadaClicked(tada)
            }

            binding.imageEdit.setOnClickListener {
                onEditTadaClicked(tada)
            }
        }
    }

    fun onTadaClicked(tada: Tada) {
        tadaHandler.onTadaClicked(tada)
    }

    fun onEditTadaClicked(tada: Tada){
        tadaHandler.onEditTadaClicked(tada)
    }
}