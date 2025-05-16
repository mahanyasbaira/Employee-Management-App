package com.vishwaagrotech.digitalhr.ui.dashboard

import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentProjectBinding
import com.vishwaagrotech.digitalhr.handler.ProjectHandler
import com.vishwaagrotech.digitalhr.handler.TaskHandler
import com.vishwaagrotech.digitalhr.model.Project
import com.vishwaagrotech.digitalhr.model.Task
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.ProjectListAdapter
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.TaskListAdapter
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.HeaderViewModel
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.ProjectViewModel
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ProjectFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, TaskHandler,
    ProjectHandler {
    lateinit var binding: FragmentProjectBinding

    private val headerModel: HeaderViewModel by viewModels()
    private val model: ProjectViewModel by viewModels()

    lateinit var projectListAdapter: ProjectListAdapter
    var projectList: ArrayList<Project> = ArrayList()

    lateinit var taskListAdapter: TaskListAdapter
    var taskList: ArrayList<Task> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_project, container, false)
        binding.handler = this

        binding.layoutHeader.headerModel = headerModel
        binding.viewmodel = model
        binding.lifecycleOwner = viewLifecycleOwner
        binding.layoutHeader.lifecycleOwner = viewLifecycleOwner

        toolbarConfig()
        return binding.root
    }

    private fun toolbarConfig() {

        binding.toolbar.toolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)
        setHasOptionsMenu(true)
    }

    fun makeProjectList() {
        projectListAdapter = ProjectListAdapter(projectList, 1, this)
        binding.recyclerViewProjects.apply {
            adapter = projectListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        taskListAdapter = TaskListAdapter(taskList, this)
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

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        binding.layoutHeader.cardImage.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }
        binding.layoutHeader.layoutDetail.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }

        observeProjectOverview()
        observeProjectList()
        observeTaskList()

        makeProjectList()

        model.getProjectOverview()
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
            model.taskList.collectLatest {
                if (it.isNotEmpty()) {
                    binding.layoutTask.visibility = View.VISIBLE
                } else {
                    binding.layoutTask.visibility = View.INVISIBLE
                }
                taskList.clear()
                taskList.addAll(it)
                taskListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeProjectList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.projectList.collectLatest {
                if (it.isNotEmpty()) {
                    binding.layoutProject.visibility = View.VISIBLE
                } else {
                    binding.layoutProject.visibility = View.INVISIBLE
                }
                projectList.clear()
                projectList.addAll(it)
                projectListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun observeProjectOverview() {

    }

    fun onAllTaskClicked() {
        findNavController().navigate(R.id.action_projectFragment_to_taskListFragment)
    }

    fun onAllProjectClicked() {
        findNavController().navigate(R.id.action_projectFragment_to_projectListFragment)
    }

    override fun onRefresh() {
        model.getProjectOverview()
    }

    override fun onTaskClicked(task: Task) {
        val bundle = Bundle()
        bundle.putString("taskId", task.id.toString())
        findNavController().navigate(R.id.action_projectFragment_to_taskDetailFragment, bundle)
    }

    override fun onProjectClicked(project: Project) {
        val bundle = Bundle()
        bundle.putString("projectId", project.id.toString())
        findNavController().navigate(R.id.action_projectFragment_to_projectDetailFragment, bundle)
    }
}