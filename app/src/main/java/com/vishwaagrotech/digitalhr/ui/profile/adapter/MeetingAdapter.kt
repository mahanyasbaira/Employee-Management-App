package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.LoaderSmallDialogBinding
import com.vishwaagrotech.digitalhr.databinding.RowMeetingBinding
import com.vishwaagrotech.digitalhr.handler.MeetingHandler
import com.vishwaagrotech.digitalhr.model.Meeting

class MeetingAdapter(
    private val data: ArrayList<Meeting?>,
    private val agendaHandler: MeetingHandler
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var VIEW_TYPE_ITEM = 0
    private var VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val binding =
                RowMeetingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    inner class ViewHolder(val binding: RowMeetingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meeting: Meeting) {
            binding.meeting = meeting

            binding.textVenue.text = "Venue: ${meeting.venue}"

            binding.recyclerviewParticipants.apply {
                adapter = ParticipantAdapter(meeting.participator)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            binding.root.setOnClickListener {
                agendaHandler.onMeetingClicked(meeting)
            }

            binding.textMore.apply {
                if (meeting.participator.size > 3) {
                    text = "+${meeting.participator.size - 3} more"
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }

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
}