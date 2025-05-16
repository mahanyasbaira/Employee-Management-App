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
import com.vishwaagrotech.digitalhr.databinding.FragmentNoticeBinding
import com.vishwaagrotech.digitalhr.model.Notice
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.NoticeAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.NoticeViewModel
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
class NoticeFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentNoticeBinding
    private val model: NoticeViewModel by viewModels()

    lateinit var noticeAdapter: NoticeAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private var noticeList = ArrayList<Notice?>()

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
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notice, container, false)
        binding.handler = this

        toolbarConfig()

        PAGE = 1
        ISLOADING = false
        ISLAST = false
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeNotice()
        observeNotice()
        declarePagination()
        loadNotice()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Notice"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun makeNotice() {
        noticeAdapter = NoticeAdapter(noticeList)
        binding.recyclerviewNotice.apply {
            adapter = noticeAdapter
            layoutManager = this@NoticeFragment.layoutManager
        }
    }

    private fun loadNotice() {
        ISLOADING = true
        model.fetchNotification(PER_PAGE, PAGE)
    }

    private fun declarePagination() {
        binding.recyclerviewNotice.addOnScrollListener(object : PaginationScrollListener(
            layoutManager
        ) {
            override fun loadMoreItems() {
                noticeList.add(null)
                noticeAdapter.notifyItemInserted(noticeList.size - 1);
                loadNotice()
            }

            override fun isLastPage(): Boolean {
                return ISLAST
            }

            override fun isLoading(): Boolean {
                return ISLOADING
            }
        })
    }

    private fun observeNotice() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.noticeResponse.collectLatest {
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
                                        noticeList.removeAt(noticeList.size - 1);
                                        val scrollPosition: Int = noticeList.size
                                        noticeAdapter.notifyItemRemoved(scrollPosition)
                                    }

                                    if (noticeList.size == 0) {
                                        binding.recyclerviewNotice.visibility = View.GONE
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
                                binding.recyclerviewNotice.visibility = View.VISIBLE
                                binding.lost.dialogLostConnection.visibility = View.GONE

                                if (PAGE != 1) {
                                    noticeList.removeAt(noticeList.size - 1);
                                    val scrollPosition: Int = noticeList.size
                                    noticeAdapter.notifyItemRemoved(scrollPosition)
                                }

                                if (it.result.size > 0) {
                                    if (PAGE == 1) {
                                        noticeList.clear()
                                    }
                                    noticeList.addAll(model.convertNotice(it.result))
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

    override fun onRefresh() {
        binding.refreshPage.setRefreshing(true)
        PAGE = 1;
        loadNotice()
    }
}