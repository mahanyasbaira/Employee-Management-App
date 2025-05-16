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
import android.widget.Toast
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
import com.vishwaagrotech.digitalhr.databinding.FragmentTaskDetailBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.handler.ChecklistHandler
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.model.Checklist
import com.vishwaagrotech.digitalhr.ui.project.adapter.ChecklistDetailListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.ProjectAttachmentListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.ProjectAttachmentListImageAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.TeamListAdapter
import com.vishwaagrotech.digitalhr.ui.project.viewmodel.TaskDetailViewModel
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TaskDetailFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener,
    ChecklistHandler, AttachmentHandler {
    lateinit var binding: FragmentTaskDetailBinding

    lateinit var checklistAdapter: ChecklistDetailListAdapter
    var checkList: ArrayList<Checklist> = ArrayList()

    private val model : TaskDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_task_detail, container, false)
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
        checklistAdapter = ChecklistDetailListAdapter(checkList,this);
        binding.recyclerViewTasks.apply {
            adapter = checklistAdapter
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
                    LoadingUtils.showDialog(requireContext(),false)
                } else {
                    binding.refreshPage.setRefreshing(false)
                    LoadingUtils.hideDialog()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.toggleTask.collectLatest {
                if (it) {
                    Toast.makeText(requireContext(),"Task has been marked as completed",Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.taskDetail.collectLatest {

                Log.e("imageload",it.toString())
                val teamList = ArrayList<Bitmap>()
                for (team in it.team) {
                    Glide.with(requireContext()).asBitmap().load(team.image)
                        .apply(RequestOptions.circleCropTransform())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                teamList.add(resource)

                                if (teamList.size == it.team.size) {
                                    binding.overlapImageTeam.imageList = teamList
                                }
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                binding.toolbar.toolbar.title = it.projectName

                checkList.clear()
                checkList.addAll(it.checklist)
                checklistAdapter.notifyDataSetChanged()

                if(it.hasCheckList){
                    binding.layoutChecklist.visibility = View.VISIBLE
                    binding.buttonStatusToggle.visibility = View.GONE
                }else{
                    if(it.status == "Completed"){
                        binding.buttonStatusToggle.visibility = View.GONE
                    }else{

                        binding.buttonStatusToggle.visibility = View.VISIBLE
                    }
                    binding.layoutChecklist.visibility = View.GONE
                }

            }
        }
    }

    override fun onRefresh() {
        model.getTaskDetailOverview(arguments!!.getString("taskId")!!)
    }

    override fun onChecklistClicked(checklist: Checklist) {
        model.toggleCheckList(checklist)
    }

    fun onTeamClicked(){
        val dialog = BottomSheetDialog(requireContext())

        val sheetBinding: DialogTeamsAndLeadersBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_teams_and_leaders,
                null,
                false
            )

        sheetBinding.handler2 = this
        dialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        sheetBinding.imageClose2.setOnClickListener {
            dialog.dismiss()
        }

        val team = ArrayList(model.taskDetail.value.team)

        val teamAdapter = TeamListAdapter(team);
        sheetBinding.recyclerViewTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        sheetBinding.recyclerViewLeaders.visibility = View.GONE
        sheetBinding.layoutLeaderTitle.visibility = View.GONE


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

        sheetBinding.handler2 = this

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.imageClose2.visibility = View.GONE

        val attachment = ArrayList(model.taskDetail.value.attachment)

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

    fun onCommentClicked(){
        val bundle = Bundle()
        bundle.putString("taskId", model.taskDetail.value.id.toString())

        val action = TaskDetailFragmentDirections.actionTaskDetailFragmentToCommentListFragment(model.taskDetail.value.team.toTypedArray(),model.taskDetail.value.id.toString())
        findNavController().navigate(action)
    }
}