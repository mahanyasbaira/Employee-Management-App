package com.vishwaagrotech.digitalhr.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentNotificationBinding
import com.vishwaagrotech.digitalhr.model.Notice
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.NoticeAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.NotificationViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.PaginationScrollListener
import com.vishwaagrotech.digitalhr.utils.makeToast
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class NotificationFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentNotificationBinding
    private val model: NotificationViewModel by viewModels()

    lateinit var noticeAdapter: NoticeAdapter

    private var notificationList = ArrayList<Notice?>()

    private lateinit var layoutManager: LinearLayoutManager

    companion object {
        var PER_PAGE = 10
        var PAGE = 1
        var ISLOADING = false
        var ISLAST = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        binding.handler = this

        toolbarConfig()

        PAGE = 1
        ISLOADING = false
        ISLAST = false
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeNotice()
        observeNotification()
        declarePagination()
        loadNotification()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Notification"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    private fun makeNotice() {
        noticeAdapter = NoticeAdapter(notificationList)
        binding.recyclerviewNotification.apply {
            adapter = noticeAdapter
            layoutManager = this@NotificationFragment.layoutManager
        }
    }

    private fun loadNotification() {
        ISLOADING = true
        model.fetchNotification(PER_PAGE, PAGE)
    }

    private fun declarePagination() {
        binding.recyclerviewNotification.addOnScrollListener(object : PaginationScrollListener(
            layoutManager
        ) {
            override fun loadMoreItems() {
                notificationList.add(null)
                noticeAdapter.notifyItemInserted(notificationList.size - 1);
                loadNotification()
            }

            override fun isLastPage(): Boolean {
                return ISLAST
            }

            override fun isLoading(): Boolean {
                return ISLOADING
            }
        })
    }

    private fun observeNotification() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.notificationResponse.collectLatest {
                when(it){
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                            }
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                if (it != null) {
                                    ISLOADING = false
                                    if (PAGE != 1) {
                                        notificationList.removeAt(notificationList.size - 1);
                                        val scrollPosition: Int = notificationList.size
                                        noticeAdapter.notifyItemRemoved(scrollPosition)
                                    }

                                    if (notificationList.size == 0) {
                                        binding.recyclerviewNotification.visibility = View.GONE
                                        binding.lost.dialogLostConnection.visibility = View.VISIBLE
                                    }
                                }
                            }

                            requireActivity().makeToast(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(true)
                            }
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                            }
                            if (it.result != null) {
                                ISLOADING = false
                                binding.recyclerviewNotification.visibility = View.VISIBLE
                                binding.lost.dialogLostConnection.visibility = View.GONE

                                if (PAGE != 1) {
                                    notificationList.removeAt(notificationList.size - 1);
                                    val scrollPosition: Int = notificationList.size
                                    noticeAdapter.notifyItemRemoved(scrollPosition)
                                }

                                if (it.result.size > 0) {
                                    if (PAGE == 1) {
                                        notificationList.clear()
                                    }
                                    notificationList.addAll(model.convertNotification(it.result))
                                    PAGE++
                                    noticeAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        binding.refreshPage.setRefreshing(true)
        PAGE = 1;
        loadNotification()
    }
}