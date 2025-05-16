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
import com.vishwaagrotech.digitalhr.databinding.FragmentHolidaysBinding
import com.vishwaagrotech.digitalhr.ui.profile.adapter.HolidayAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.HolidayViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.makeToast
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HolidayFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentHolidaysBinding
    private val model: HolidayViewModel by viewModels()

    lateinit var holidayAdapter: HolidayAdapter

    private val holidayList: ArrayList<com.vishwaagrotech.digitalhr.model.Holiday> = ArrayList()
    private val filterHolidayList: ArrayList<com.vishwaagrotech.digitalhr.model.Holiday> = ArrayList()

    companion object {
        var new = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_holidays, container, false)
        binding.handler = this

        toolbarConfig()
        new = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterClicked()
        makeHolidays()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        observeHoliday()
        onRefresh()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Holidays"
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

    private fun makeHolidays() {
        holidayAdapter = HolidayAdapter(filterHolidayList)
        binding.recyclerviewHolidays.apply {
            adapter = holidayAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun filterClicked() {
        binding.radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.radio_new -> {
                    new = true
                    filterHoliday()
                }
                R.id.radio_old -> {
                    new = false
                    filterHoliday()
                }
            }
        }
    }

    private fun observeHoliday() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.holidayResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)

                            requireActivity().makeToast(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(true)
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)

                            if (it.result != null) {
                                holidayList.clear()
                                holidayList.addAll(model.convertHolidayFormat(it.result))

                                filterHoliday()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun filterHoliday() {
        filterHolidayList.clear()
        filterHolidayList.addAll(model.getFilterHolidayList(holidayList, new))

        holidayAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        model.getHolidayResponse()
    }
}