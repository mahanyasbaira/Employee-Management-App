package com.vishwaagrotech.digitalhr.ui.dashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.LoaderSmallDialogBinding
import com.vishwaagrotech.digitalhr.databinding.RowNoticeBinding
import com.vishwaagrotech.digitalhr.model.Notice

class NoticeAdapter(
    private val data: ArrayList<Notice?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var VIEW_TYPE_ITEM = 0
    private var VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val binding =
                RowNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            binding.handler = this
            return ViewHolder(binding)
        } else {
            val binding =
                LoaderSmallDialogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LoadViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.bind(data[position]!!)
        } else if (holder is LoadViewHolder) {
            showLoadingView(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notice: Notice) {
            binding.notice = notice
        }
    }

    inner class LoadViewHolder(val binding: LoaderSmallDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (data.get(position) == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    fun showLoadingView(view: LoadViewHolder, position: Int) {

    }

    fun onNoticeClicked(notice: Notice) {

    }

}