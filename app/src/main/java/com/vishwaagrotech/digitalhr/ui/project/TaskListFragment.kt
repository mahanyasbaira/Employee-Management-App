package com.vishwaagrotech.digitalhr.ui.project

import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentTaskListBinding
import com.vishwaagrotech.digitalhr.handler.TaskHandler
import com.vishwaagrotech.digitalhr.model.Task
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.TaskListAdapter
import com.vishwaagrotech.digitalhr.ui.project.viewmodel.TaskViewModel
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TaskListFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, TaskHandler {
    lateinit var binding: FragmentTaskListBinding

    private val model: TaskViewModel by viewModels()

    lateinit var taskListAdapter: TaskListAdapter
    var taskList : ArrayList<Task> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_list, container, false)
        binding.handler = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = model

        toolbarConfig()
        return binding.root
    }

    private fun toolbarConfig() {

        binding.toolbar.toolbar.title = "Assigned Task"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    fun makeTaskList(){
        taskListAdapter = TaskListAdapter(taskList,this);
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

        model.getTaskListOverview()
    }

    private fun observeTaskList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loading.collectLatest {
                if(it){
                    binding.refreshPage.setRefreshing(true)
                }else{
                    binding.refreshPage.setRefreshing(false)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.taskList.collectLatest {
                Log.e("TaskList",it.toString())
                taskList.clear()
                taskList.addAll(it)
                taskListAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRefresh() {
        model.getTaskListOverview()
    }

    override fun onTaskClicked(task: Task) {
        val bundle = Bundle()
        bundle.putString("taskId", task.id.toString())
        findNavController().navigate(R.id.action_taskListFragment_to_taskDetailFragment, bundle)
    }
}