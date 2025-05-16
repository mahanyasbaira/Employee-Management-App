package com.vishwaagrotech.digitalhr.ui.project

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogAttachmentsBinding
import com.vishwaagrotech.digitalhr.databinding.DialogTeamsAndLeadersBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentProjectDetailBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.handler.TaskHandler
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.model.Task
import com.vishwaagrotech.digitalhr.ui.project.adapter.ProjectAttachmentListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.ProjectAttachmentListImageAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.TaskDetailListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.TeamListAdapter
import com.vishwaagrotech.digitalhr.ui.project.viewmodel.ProjectDetailViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProjectDetailFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, TaskHandler,
    AttachmentHandler {
    lateinit var binding: FragmentProjectDetailBinding

    lateinit var taskListAdapter: TaskDetailListAdapter
    var taskList: ArrayList<Task> = ArrayList()

    private val model: ProjectDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_project_detail, container, false)
        binding.handler = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = model

        toolbarConfig()
        return binding.root
    }

    private fun toolbarConfig() {

        binding.toolbar.toolbar.title = ""
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    fun makeTaskList() {
        taskListAdapter = TaskDetailListAdapter(taskList, this);
        binding.recyclerViewTasks.apply {
            adapter = taskListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notification, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notification -> {
                findNavController().navigate(R.id.global_notification)
                super.onOptionsItemSelected(item)
            }

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                super.onOptionsItemSelected(item)
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)
        observeTaskList()

        makeTaskList()
        onRefresh()
    }

    private fun observeTaskList() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loading.collectLatest {
                if (it) {
                    binding.refreshPage.setRefreshing(true)
                } else {
                    binding.refreshPage.setRefreshing(false)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.projectDetail.collectLatest {

                Log.e("imageload", it.toString())
                val teamList = ArrayList<Bitmap>()
                for (team in it.teams) {
                    Glide.with(requireContext()).asBitmap().load(team.image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                teamList.add(resource)

                                if (teamList.size == it.teams.size) {
                                    binding.overlapImageTeam.imageList = teamList
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                val leaderList = ArrayList<Bitmap>()
                for (team in it.leader) {
                    Glide.with(requireContext()).asBitmap().load(team.image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                leaderList.add(resource)

                                if (leaderList.size == it.leader.size) {
                                    binding.overlapImageLeader.imageList = leaderList
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                taskList.clear()
                taskList.addAll(it.task)
                taskListAdapter.notifyDataSetChanged()

            }
        }
    }

    override fun onRefresh() {
        model.getTaskListOverview(arguments!!.getString("projectId")!!)
    }

    override fun onTaskClicked(task: Task) {
        val bundle = Bundle()
        bundle.putString("taskId", task.id.toString())
        findNavController().navigate(
            R.id.action_projectDetailFragment_to_taskDetailFragment,
            bundle
        )
    }

    fun onTeamClicked() {
        val dialog = BottomSheetDialog(requireContext())

        val sheetBinding: DialogTeamsAndLeadersBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_teams_and_leaders,
                null,
                false
            )

        sheetBinding.handler = this
        dialog.behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.imageClose2.visibility = View.GONE

        val leader = ArrayList(model.projectDetail.value.leader)
        val team = ArrayList(model.projectDetail.value.teams)

        val leaderAdapter = TeamListAdapter(leader);
        sheetBinding.recyclerViewLeaders.apply {
            adapter = leaderAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        val teamAdapter = TeamListAdapter(team);
        sheetBinding.recyclerViewTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }


        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    fun onAttachmentClicked() {
        val dialog = BottomSheetDialog(requireContext())

        val sheetBinding: DialogAttachmentsBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_attachments,
                null,
                false
            )

        sheetBinding.handler = this

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.imageClose2.visibility = View.GONE

        val attachment = ArrayList(model.projectDetail.value.attachment)

        val file = ArrayList(attachment.filter {
            it.type != "image"
        })

        val image = ArrayList(attachment.filter {
            it.type == "image"
        })

        val fileAdapter = ProjectAttachmentListAdapter(file, this);
        sheetBinding.recyclerViewFiles.apply {
            adapter = fileAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        val imageAdapter = ProjectAttachmentListImageAdapter(image, this);
        sheetBinding.recyclerViewImage.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(context, 2)
        }


        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    override fun onAttachmentClicked(attachment: Attachment) {

    }

    override fun onRemoveAttachmentClicked(attachment: Attachment) {

    }

    override fun onLoadAttachmentClicked(attachment: Attachment) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url))
        startActivity(browserIntent)
    }
}