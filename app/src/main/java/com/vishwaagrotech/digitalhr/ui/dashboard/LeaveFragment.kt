package com.vishwaagrotech.digitalhr.ui.dashboard

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogIssueLeaveBinding
import com.vishwaagrotech.digitalhr.databinding.DialogTimePickerBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentCheckInBinding
import com.vishwaagrotech.digitalhr.model.Leave
import com.vishwaagrotech.digitalhr.model.LeaveType
import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.mapper.LeaveListMapper
import com.vishwaagrotech.digitalhr.repository.mapper.LeaveTypeMapper
import com.vishwaagrotech.digitalhr.repository.network.api.leaverequestlist.LeaveRequestList
import com.vishwaagrotech.digitalhr.repository.network.api.leavetype.LeaveTypeDomain
import com.vishwaagrotech.digitalhr.ui.dashboard.adapter.LeaveTypeAdapter
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.*
import com.vishwaagrotech.digitalhr.ui.profile.adapter.LeaveAdapter
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.showConfirmDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class LeaveFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentCheckInBinding
    private val headerModel: HeaderViewModel by viewModels()

    private val leaveViewModel: LeaveViewModel by viewModels()

    private var leaveTypeList = ArrayList<LeaveType>()

    lateinit var leaveTypeAdapter: LeaveTypeAdapter
    lateinit var leaveAdapter: LeaveAdapter

    private var leaveRequestList = ArrayList<Leave>()

    lateinit var dataStoreManager: DataStoreManager

    companion object {
        var month = 0 // 0 = yearly report 1 = Current month report
        var leaveType = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_check_in, container, false)
        binding.handler = this

        binding.layoutHeader.headerModel = headerModel
        binding.layoutHeader.lifecycleOwner = viewLifecycleOwner

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLeaveType()
        observeLeaveRequest()
        observeLeaveRequestList()
        makeLeaveType()
        makeLeave()
        headerModel.getUserDetail()


        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        binding.layoutHeader.cardImage.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }
        binding.layoutHeader.layoutDetail.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }

        onRefresh()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            binding.buttonUrgentLeave.isClickable = leaveViewModel.getStatus()

            if(leaveViewModel.getStatus()){
                binding.buttonUrgentLeave.alpha = 1f
            }else{
                binding.buttonUrgentLeave.alpha = 0.5f
            }

        }

        binding.radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            if (i == R.id.rbMonth) {
                val now = Calendar.getInstance()
                val currentMonth: Int = now.get(Calendar.MONTH)
                month = currentMonth + 1
                leaveViewModel.getLeaveRequestListResponse(leaveType, month)
            } else if (i == R.id.rbYear) {
                month = 0
                leaveViewModel.getLeaveRequestListResponse(leaveType, month)
            }
        }

        binding.rbYear.isChecked = true

        binding.spinnerLeave.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                leaveType = filterList[p2].id
                leaveViewModel.getLeaveRequestListResponse(leaveType, month)
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

    fun makeLeaveType() {
        leaveTypeAdapter = LeaveTypeAdapter(leaveTypeList)
        binding.recyclerviewLeaveType.apply {
            adapter = leaveTypeAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    var filterList = ArrayList<LeaveType>()
    private fun observeLeaveType() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            leaveViewModel.leaveType.collectLatest {
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
                        val data =
                            LeaveTypeMapper.mapToEntityList(it.result.data as ArrayList<LeaveTypeDomain>)
                        leaveTypeList.clear()

                        leaveTypeList.addAll(data)

                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            leaveTypeAdapter.notifyDataSetChanged()

                            filterList.clear()
                            filterList.add(LeaveType(0, "All", 0, 0, false, true))
                            filterList.addAll(leaveTypeList)

                            val adp1: ArrayAdapter<LeaveType> = ArrayAdapter<LeaveType>(
                                requireContext(),
                                android.R.layout.simple_list_item_1, filterList
                            )

                            adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            binding.spinnerLeave.adapter = adp1
                        }
                    }
                }
            }
        }
    }


    fun makeLeave() {
        leaveAdapter = LeaveAdapter(leaveRequestList)
        binding.recyclerviewLeaveList.apply {
            adapter = leaveAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun showLeaveSheet() {
        val dialog = BottomSheetDialog(requireContext())

        val sheetBinding: DialogIssueLeaveBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_issue_leave,
                null,
                false
            )
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED


        val filterList = ArrayList<LeaveType>()

        for (type in leaveTypeList) {
            if (type.status) {
                filterList.add(type)
            }
        }

        val adp1: ArrayAdapter<LeaveType> = ArrayAdapter<LeaveType>(
            requireContext(),
            android.R.layout.simple_list_item_1, filterList
        )

        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sheetBinding.spinnerLeave.adapter = adp1

        sheetBinding.buttonDatePicker1.setOnClickListener {
            showDatePicker(sheetBinding.buttonDatePicker1)
        }

        sheetBinding.buttonDatePicker2.setOnClickListener {
            showDatePicker(sheetBinding.buttonDatePicker2)
        }

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.buttonSubmitLeave.setOnClickListener {
            val from = sheetBinding.buttonDatePicker1.text.toString()
            val to = sheetBinding.buttonDatePicker2.text.toString()
            val id = filterList[sheetBinding.spinnerLeave.selectedItemPosition].id
            val reason = sheetBinding.editReason.text.toString()

            if (!from.contentEquals("")
                && !to.contentEquals("")
                && !reason.contentEquals("")
            ) {
                leaveViewModel.getLeaveRequestResponse(from, to, id, reason, 0)
            } else {
                Snackbar.make(sheetBinding.root, "All field must be filled", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    fun showEarlyLeaveSheet() {
        val dialog = BottomSheetDialog(requireContext())
        var time = ""
        val sheetBinding: DialogTimePickerBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_time_picker,
                null,
                false
            )
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val filterList = ArrayList<LeaveType>()

        for (type in leaveTypeList) {
            if (type.status && type.early_exit) {
                filterList.add(type)
            }
        }

        val adp1: ArrayAdapter<LeaveType> = ArrayAdapter<LeaveType>(
            requireContext(),
            android.R.layout.simple_list_item_1, filterList
        )

        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sheetBinding.spinnerLeave.adapter = adp1

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.timepicker.setOnTimeChangedListener { timePicker, i, i2 ->
            val calendar = Calendar.getInstance()
            calendar.time = Date() // Set your date object here

            calendar.set(Calendar.HOUR_OF_DAY, i)
            calendar.set(Calendar.MINUTE, i2)
            calendar.set(Calendar.SECOND, 0)
            calendar.time

            val format = SimpleDateFormat("yyyy-MM-d HH:mm:ss")

            time = format.format(calendar.time)

            Log.e("time", time)
        }

        sheetBinding.buttonSubmitLeave.setOnClickListener {
            val from = time
            val id = filterList[sheetBinding.spinnerLeave.selectedItemPosition].id
            val reason = sheetBinding.editReason.text.toString()

            if (!from.contentEquals("")
                && !reason.contentEquals("")
            ) {
                leaveViewModel.getLeaveRequestResponse(from, from, id, reason, 1)
            } else {
                Snackbar.make(sheetBinding.root, "All field must be filled", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    private fun observeLeaveRequest() {
        viewLifecycleOwner.lifecycleScope.launch {
            leaveViewModel.leaveRequest.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            LoadingUtils.hideDialog()
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(true)
                            LoadingUtils.showDialog(context, false)
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog(it.result.message)
                        }
                    }
                }
            }
        }
    }

    fun showDatePicker(btn: MaterialButton) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { view, myear, mmonth, mdayOfMonth ->

            },
            year,
            month,
            day
        )

        datePickerDialog.setOnDateSetListener { datePicker, year, month, day ->
            btn.text = "$year-${month.plus(1)}-$day 00:00:00"
        }
        datePickerDialog.show()
    }

    fun observeLeaveRequestList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            leaveViewModel.leaveRequestList.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {}
                    is EventHandler.Failure -> {}
                    is EventHandler.Loading -> {}
                    is EventHandler.Success -> {
                        leaveRequestList.clear()

                        if (it.result.data.isNotEmpty()){
                            leaveRequestList.addAll(LeaveListMapper.mapToEntityList(it.result.data as ArrayList<LeaveRequestList>))
                        }

                        withContext(Dispatchers.Main) {
                            leaveAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        leaveViewModel.getLeaveType()
    }
}