package com.vishwaagrotech.digitalhr.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentAttendanceBinding
import com.vishwaagrotech.digitalhr.model.Attendance
import com.vishwaagrotech.digitalhr.repository.mapper.AttendanceMapper
import com.vishwaagrotech.digitalhr.repository.network.api.employeeattendancereport.EmployeeAttendance
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.AttendanceHistoryAdapter
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.AttendanceViewModel
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.HeaderViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.android.material.snackbar.Snackbar
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AttendanceFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentAttendanceBinding

    private val model: AttendanceViewModel by viewModels()
    private val headerModel: HeaderViewModel by viewModels()

    lateinit var attendanceHistoryAdapter: AttendanceHistoryAdapter
    private var attendanceReport: ArrayList<Attendance> = ArrayList()

    companion object{
        var month = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attendance, container, false)
        binding.handler = this

        binding.layoutHeader.headerModel = headerModel
        binding.layoutHeader.lifecycleOwner = viewLifecycleOwner

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeAttendance()
        loadAttendance()
        headerModel.getUserDetail()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        binding.layoutHeader.cardImage.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }
        binding.layoutHeader.layoutDetail.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }

        binding.spinnerMonth.setSelection(Integer.parseInt(model.getCurrentDate()) - 1, true)
        month = Integer.parseInt(model.getCurrentDate())
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                month = p2 + 1
                onRefresh()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

    }

    private fun toolbarConfig() {

        binding.toolbar.toolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)
        setHasOptionsMenu(true)
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

    private fun makeAttendance() {
        attendanceHistoryAdapter = AttendanceHistoryAdapter(attendanceReport)
        binding.recyclerviewAttendance.apply {
            adapter = attendanceHistoryAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun loadAttendance() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.attendance.collectLatest { event ->
                when (event) {
                    is EventHandler.Success -> {
                        binding.refreshPage.setRefreshing(false)
                        val data = event.result.data.employee_attendance

                        if (event.result.data.employee_today_attendance!=null){
                            val time = event.result.data.employee_today_attendance
                            binding.textStart.text = time.check_in_at
                            binding.textEnd.text = time.check_out_at

                            if (!time.check_in_at.contentEquals("-") && time.check_out_at.contentEquals("-")) {
                                binding.progressViewHorizontal.setStartColor(Color.parseColor("#DC3772"))
                                binding.progressViewHorizontal.setEndColor(Color.parseColor("#D25A85"))

                            } else {
                                binding.progressViewHorizontal.setStartColor(Color.parseColor("#036eb7"))
                                binding.progressViewHorizontal.setEndColor(Color.parseColor("#02b8f3"))
                            }

                            binding.progressViewHorizontal.progress = model.checkAttendanceMinute(time.check_in_at, time.check_out_at)
                            binding.textProgressText.text =
                                model.checkAttendanceHour(time.check_in_at, time.check_out_at)
                        }

                        attendanceReport.clear()
                        attendanceReport.addAll(AttendanceMapper.mapToEntityList(data as ArrayList<EmployeeAttendance>))
                        attendanceHistoryAdapter.notifyDataSetChanged()

                        if (attendanceReport.size == 0) {
                            Snackbar.make(binding.root, "No data found", Snackbar.LENGTH_SHORT)
                                .show()
                        }

                    }
                    is EventHandler.Failure -> {
                        binding.refreshPage.setRefreshing(false)
                        Snackbar.make(binding.root, event.errorText, Snackbar.LENGTH_SHORT).show()
                    }
                    is EventHandler.Loading -> {
                        binding.refreshPage.setRefreshing(true)
                    }
                    EventHandler.Empty -> {
                        binding.refreshPage.setRefreshing(false)
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        binding.refreshPage.setRefreshing(true)
        model.attendanceResponse(month)
    }
}