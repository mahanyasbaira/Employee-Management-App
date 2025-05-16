package com.vishwaagrotech.digitalhr.ui.profile

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.CalendarUtils
import com.applandeo.materialcalendarview.EventDay
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentLeaveCalendarBinding
import com.vishwaagrotech.digitalhr.model.LeaveEvent
import com.vishwaagrotech.digitalhr.model.LeaveEventEmployee
import com.vishwaagrotech.digitalhr.ui.profile.adapter.LeaveEmployeeAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.LeaveCalendarViewModel
import com.vishwaagrotech.digitalhr.utils.Constant.formatter
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class LeaveCalendarFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentLeaveCalendarBinding
    private val leaveCalendarModel: LeaveCalendarViewModel by viewModels()

    lateinit var leaveEmployeeAdapter: LeaveEmployeeAdapter

    private var leaveCalendarList = ArrayList<LeaveEventEmployee>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_leave_calendar, container, false)
        binding.handler = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        makeLeaves()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        observeCalendarEvent()
        observeCalendarLeaveByDayEvent()
        onRefresh()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Leave Calendar"
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

    fun configureCalendar() {
        val maxcalendar = Calendar.getInstance()
        val minCalendar = Calendar.getInstance()

        minCalendar.set(Calendar.DATE, 0)

        maxcalendar.set(Calendar.MONTH, maxcalendar.get(Calendar.MONTH) + 1)
        maxcalendar.set(Calendar.DATE, 32)

        binding.calendarView.setMaximumDate(maxcalendar)
        binding.calendarView.setMinimumDate(minCalendar)

        binding.calendarView.setOnDayClickListener {
            leaveCalendarModel.getLeaveCalendarByDayResponse(formatter.format(it.calendar.time))
        }
    }

    private fun makeLeaveCalendar(leaves: ArrayList<LeaveEvent>) {
        val events: ArrayList<EventDay> = ArrayList()

        val typeFace: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.google_sans)

        for (event in leaves) {

            if (event.total != 0) {
                val draw = CalendarUtils.getDrawableText(
                    context,
                    event.total.toString(),
                    typeFace,
                    R.color.grey_a7,
                    10
                );
                events.add(EventDay(event.date.toCalendar(), draw))
            }
        }

        binding.calendarView.setEvents(events)

        configureCalendar()
    }

    private fun Date?.toCalendar(): Calendar? {
        return this?.let { date ->
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar
        }
    }

    private fun makeLeaves() {
        leaveEmployeeAdapter = LeaveEmployeeAdapter(leaveCalendarList)
        binding.recyclerviewLeaveEmployee.apply {
            adapter = leaveEmployeeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observeCalendarEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            leaveCalendarModel.leaveCalendar.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(true)
                        }
                    }
                    is EventHandler.Success -> {
                        val data = it.result.data
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            makeLeaveCalendar(
                                leaveCalendarModel.convertDomainToEntityLeaveCalendar(
                                    data
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeCalendarLeaveByDayEvent() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            leaveCalendarModel.leaveCalendarByDay.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main){
                            binding.refreshPage.setRefreshing(true)
                        }
                    }
                    is EventHandler.Success -> {
                        val data = it.result.data

                        leaveCalendarList.clear()
                        leaveCalendarList.addAll(
                            leaveCalendarModel.convertDomainToEntityLeaveCalendarByDay(
                                data
                            )
                        )

                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            leaveEmployeeAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        binding.refreshPage.post {
            binding.refreshPage.setRefreshing(true)
            leaveCalendarModel.getLeaveCalendarResponse()
            leaveCalendarModel.getLeaveCalendarByDayResponse(formatter.format(Date()))
        }
    }
}