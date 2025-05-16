package com.vishwaagrotech.digitalhr.ui.dashboard.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vishwaagrotech.digitalhr.databinding.RowProjectBinding
import com.vishwaagrotech.digitalhr.databinding.RowProjectFullBinding
import com.vishwaagrotech.digitalhr.handler.ProjectHandler
import com.vishwaagrotech.digitalhr.model.Project

class ProjectListAdapter(
    private val data: ArrayList<Project>,
    private val type: Int,
    private val projectHandler: ProjectHandler
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (type) {
            1 -> {
                val binding =
                    RowProjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                binding.handler = this
                ViewHolder(binding)
            }

            2 -> {
                val binding =
                    RowProjectFullBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                binding.handler = this
                ViewHolderFull(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(data[position])
            }

            is ViewHolderFull -> {
                holder.bind(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.project = project

            val imageList = ArrayList<Bitmap>()
            for (team in project.teams) {
                Glide.with(binding.root.context).asBitmap().load(team.image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            imageList.add(resource)

                            if (imageList.size == project.teams.size) {
                                binding.overlapImageTeam.imageList = imageList
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                    })
            }


            binding.root.setOnClickListener {
                projectHandler.onProjectClicked(project)
            }
        }
    }

    inner class ViewHolderFull(val binding: RowProjectFullBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.project = project

            val imageList = ArrayList<Bitmap>()
            for (team in project.teams) {
                Glide.with(binding.root.context).asBitmap().load(team.image)
                    .apply(RequestOptions.circleCropTransform())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            imageList.add(resource)

                            if (imageList.size == project.teams.size) {
                                binding.overlapImageTeam.imageList = imageList
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                    })
            }

            binding.root.setOnClickListener {
                projectHandler.onProjectClicked(project)
            }
        }
    }
}