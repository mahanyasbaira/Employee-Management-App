package com.vishwaagrotech.digitalhr.ui.dashboard

import android.Manifest
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogCheckInBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentHomeBinding
import com.vishwaagrotech.digitalhr.repository.network.api.dashboard.EmployeeWeeklyReport
import com.vishwaagrotech.digitalhr.repository.network.api.dashboard.OfficeTime
import com.vishwaagrotech.digitalhr.repository.network.api.dashboard.Overview
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.HeaderViewModel
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.HomeViewModel
import com.vishwaagrotech.digitalhr.ui.form.FormActivity
import com.vishwaagrotech.digitalhr.utils.*
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener {
    lateinit var binding: FragmentHomeBinding

    private val model: HomeViewModel by viewModels()
    private val headerModel: HeaderViewModel by viewModels()


    lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        var isSecure = false
        var latittude: Double = 0.00
        var longitude: Double = 0.00
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.handler = this

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.layoutHeader.headerModel = headerModel
        binding.layoutHeader.lifecycleOwner = viewLifecycleOwner
        binding.lifecycleOwner = viewLifecycleOwner

        checkLocation()
        registerLocation()

        toolbarConfig()
        showUserProfile()
        checkSecurity()

        observeAttendanceStatus()
        observeAttendanceStatusCheckOut()
        observeAttendance()
        observeError()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("EEEE , MMMM d , yyyy")
            val current = LocalDateTime.now().format(formatter)

            binding.textCurrentDate.text = current;
        } else {
            binding.textCurrentDate.text = "";
        }


        onRefresh()
        binding.layoutHeader.cardImage.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }
        binding.layoutHeader.layoutDetail.setOnClickListener {
            findNavController().navigate(R.id.global_profile)
        }
        //makeAlarm()

        return binding.root
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

    private fun checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
    }

    private fun registerLocation() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            latittude = it.latitude
                            longitude = it.longitude

                            Log.e("LocationFound", "lat $latittude / long $longitude")
                        }
                    }
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            latittude = it.latitude
                            longitude = it.longitude

                            Log.e("LocationFound", "lat $latittude / long $longitude")
                        }
                    }
                }

                else -> {
                    //if location is denied by the user ask for permission
                    checkLocationPermission()
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun checkLocation() {
        val manager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(context)
                .setTitle("GPS not found") // GPS not found
                .setMessage("Do you wish to enable it to use this feature?") // Want to enable?
                .setPositiveButton(
                    "Yes"
                ) { dialogInterface, i ->
                    startActivity(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    )
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    fun showUserProfile() {
        model.person.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.layoutHeader.textUserFullname.text = it.name
                binding.layoutHeader.textUsername.text = it.username

                Glide.with(this)
                    .load(it.image)
                    .into(binding.layoutHeader.imageProfile)
            }
        }
        model.getUserDetail()
    }

    private fun observeAttendance() {

        this.lifecycleScope.launchWhenStarted {
            model.dashboardResponse.collectLatest {
                when (it) {
                    is EventHandler.Success -> {
                        if (it.result.status_code == 200) {

                            withContext(Dispatchers.Main) {
                                binding.refreshPage.setRefreshing(false)
                                makeAttendanceProgress(
                                    it.result.data.employee_today_attendance.check_in_at,
                                    it.result.data.employee_today_attendance.check_out_at
                                )

                                makeOverview(it.result.data.overview)

                                makebarchat(it.result.data.employee_weekly_report)

                                scheduleDates(it.result.data.office_time, it.result.data.shift_dates)
                            }
                        }
                    }

                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                            Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                        }
                    }

                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post { binding.refreshPage.setRefreshing(true) }
                            //LoadingUtils.showDialog(context, false)
                        }
                    }

                    EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.setRefreshing(false)
                        }
                    }
                }
            }
        }
    }

    fun scheduleDates(officetime: OfficeTime, shiftDates: ArrayList<String>) {
        val notificationScheduler = NotificationScheduler()
        notificationScheduler.cancelAllNotifications(requireContext())

        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val start = format.parse(officetime.start_time)

        val startCalendar = Calendar.getInstance()
        startCalendar.time = start!!

        val end = format.parse(officetime.end_time)

        val endCalendar = Calendar.getInstance()
        endCalendar.time = end!!

        for (dates in shiftDates) {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = format.parse(dates)

            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date!!

            val startNotification = Calendar.getInstance().apply {
                // Set the desired time for the notification
                set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR)) // Set the hour
                set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH)) // Set the hour
                set(Calendar.DATE, dateCalendar.get(Calendar.DATE)) // Set the hour
                set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY)) // Set the hour
                set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE)) // Set the minute
                set(Calendar.SECOND, 0) // Set the second
            }


            val title = "Attendance Alert"
            val description = "Office time!! Please do not forget to check In ⌛️"
           notificationScheduler.scheduleNotification(
                requireContext(),
                startNotification,
                title,
                description
            )

            val endNotification = Calendar.getInstance().apply {
                // Set the desired time for the notification
                set(Calendar.YEAR, dateCalendar.get(Calendar.YEAR)) // Set the hour
                set(Calendar.MONTH, dateCalendar.get(Calendar.MONTH)) // Set the hour
                set(Calendar.DATE, dateCalendar.get(Calendar.DATE)) // Set the hour
                set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY)) // Set the hour
                set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE)) // Set the minute
                set(Calendar.SECOND, 0) // Set the second
            }


            val title1 = "Attendance Alert"
            val description1 = "Almost done with your shift!! Please do not forget to check out 😁"
            notificationScheduler.scheduleNotification(
                requireContext(),
                endNotification,
                title1,
                description1
            )
        }
    }

    fun makeAttendanceProgress(start: String, end: String) {
        binding.textStart.text = start
        binding.textEnd.text = end

        if (!start.contentEquals("-") && end.contentEquals("-")) {

            binding.imageFingerprint.setBackgroundResource(R.drawable.button_radius_pink)
            binding.progressViewHorizontal.setStartColor(Color.parseColor("#DC3772"))
            binding.progressViewHorizontal.setEndColor(Color.parseColor("#D25A85"))

            binding.rippleButton.startRippleAnimation()
        } else {
            binding.imageFingerprint.setBackgroundResource(R.drawable.button_radius)
            binding.progressViewHorizontal.setStartColor(Color.parseColor("#036eb7"))
            binding.progressViewHorizontal.setEndColor(Color.parseColor("#02b8f3"))
            binding.rippleButton.startRippleAnimation()
        }

        binding.progressViewHorizontal.progress = model.checkAttendanceMinute(start, end)
        binding.textProgressText.text =
            model.checkAttendanceHour(start, end)
    }

    private fun makeOverview(overview: Overview) {
        binding.textPresentDay.text = overview.present_days.toString()
        binding.textLeaveDays.text = overview.total_leave_taken.toString()
        binding.textHolidays.text = overview.total_holidays.toString()
        binding.textPendingLeave.text = overview.total_pending_leaves.toString()
        binding.textTotalProjects.text = overview.total_assigned_projects.toString()
        binding.textTotalTasks.text = overview.total_pending_tasks.toString()
    }

    fun showAttendanceSheet() {
        if (isSecure) {
            checkFingerprint()
        } else {
            showDialog()
        }
    }

    private fun checkSecurity() {
        model.securityCheck.observe(viewLifecycleOwner) {
            isSecure = it
        }
        model.getSecurityCheck()
    }

    fun showDialog() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                latittude = it.latitude
                longitude = it.longitude

                Log.e("LocationFound", "lat $latittude / long $longitude")

                val dialog = BottomSheetDialog(requireContext())

                val sheetBinding: DialogCheckInBinding =
                    DataBindingUtil.inflate(
                        LayoutInflater.from(context),
                        R.layout.dialog_check_in,
                        null,
                        false
                    )

                sheetBinding.handler = this
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

                sheetBinding.imageClose.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setContentView(sheetBinding.root)
                dialog.show()
            } else {
                Toast.makeText(
                    context,
                    "Make sure your location is enabled and has permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    fun checkIn() {
        model.callUserCheckedIn(
            getCurrentNetworkDetail(),
            latittude,
            longitude
        )

    }

    private fun observeError() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.errorResponse.collectLatest {
                withContext(Dispatchers.Main) {
                    binding.refreshPage.setRefreshing(false)
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeAttendanceStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.checkInResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                        }
                    }

                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog(it.errorText)
                        }
                    }

                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.showDialog(context, false)
                        }
                    }

                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog("Checked In")
                            makeAttendanceProgress(it.result.check_in_at, it.result.check_out_at)
                        }
                    }
                }
            }
        }
    }

    private fun observeAttendanceStatusCheckOut() {
        viewLifecycleOwner.lifecycleScope.launch {

            model.checkOutResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                        }
                    }

                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog(it.errorText)
                        }
                    }

                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.showDialog(context, false)
                        }
                    }

                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().showConfirmDialog("Checked Out")
                            makeAttendanceProgress(it.result.check_in_at, it.result.check_out_at)
                        }
                    }
                }
            }
        }
    }

    fun checkOut() {
        model.callUserCheckedOut(
            getCurrentNetworkDetail(),
            latittude,
            longitude
        )
    }

    private fun getCurrentNetworkDetail(): String {
        val wifiManager = requireContext().getSystemService(WIFI_SERVICE) as WifiManager

        if (wifiManager != null) {
            val bssid = wifiManager.connectionInfo.bssid
            if (bssid.isNullOrEmpty()) {
                return ""
            }
            Log.e("Bssid", bssid)

            return bssid
        }

        return ""
    }

    fun checkFingerprint() {
        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                promptFingerPrint()
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "No Biometric found on the device", Toast.LENGTH_SHORT)
                    .show()
                biometricPrompt?.cancelAuthentication()
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "No Biometric found on the device", Toast.LENGTH_SHORT)
                    .show()
                biometricPrompt?.cancelAuthentication()
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    context,
                    "No Biometric assign on the device. Assign biometric and try again",
                    Toast.LENGTH_SHORT
                )
                    .show()
                biometricPrompt?.cancelAuthentication()
            }
        }


        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor!!,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    biometricPrompt?.cancelAuthentication()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    showDialog()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    biometricPrompt?.cancelAuthentication()
                }
            })
    }

    private var executor: Executor? = null
    private var biometricPrompt: BiometricPrompt? = null

    private fun promptFingerPrint() {


        if (Build.VERSION.SDK_INT <= 29) {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Put your fingerprint")
                .setSubtitle("Set finger print for check in and out")
                // Can't call setNegativeButtonText() and
                // setAllowedAuthenticators(... or DEVICE_CREDENTIAL) at the same time.
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                .build()
            biometricPrompt?.authenticate(promptInfo)
        } else {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Put your fingerprint")
                .setSubtitle("Set finger print for check in and out")
                // Can't call setNegativeButtonText() and
                // setAllowedAuthenticators(... or DEVICE_CREDENTIAL) at the same time.
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
                .build()
            biometricPrompt?.authenticate(promptInfo)
        }
    }

    fun makebarchat(employeeWeeklyReport: ArrayList<EmployeeWeeklyReport>) {
        val values: ArrayList<BarEntry> = ArrayList()

        Log.e("report", employeeWeeklyReport.toString())

        repeat(7) {
            if (employeeWeeklyReport.get(it) != null) {
                values.add(
                    BarEntry(
                        it.toFloat(),
                        employeeWeeklyReport[it].productive_time_in_min.toFloat() / 60
                    )
                )
            } else {
                values.add(BarEntry(it.toFloat(), 0f))
            }
        }

        val bardataset = BarDataSet(values, "Weekly")
        bardataset.valueTextColor = Color.WHITE

        val data = BarData(bardataset)
        val values1 = arrayOf("Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat")

        var count: ArrayList<String> = ArrayList()

        repeat(values.size) {
            count.add((it + 1).toString())
        }

        bardataset.valueTextSize = 0f
        bardataset.valueTextColor = Color.TRANSPARENT
        bardataset.formLineWidth = 1f

        val VORDIPLOM_COLORS = intArrayOf(
            Color.rgb(255, 255, 255)
        )

        bardataset.setColors(*VORDIPLOM_COLORS)

        val yAxis: YAxis = binding.barchart.getAxis(YAxis.AxisDependency.LEFT)
        yAxis.textColor = Color.WHITE

        val xAxis: XAxis = binding.barchart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(values1)
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 10f


        val des = Description()
        des.text = ""
        binding.barchart.renderer = RoundedBarChart(
            binding.barchart,
            binding.barchart.animator,
            binding.barchart.viewPortHandler
        )
        binding.barchart.data = data
        binding.barchart.setScaleEnabled(false)
        binding.barchart.axisLeft.axisMinimum = 0f
        binding.barchart.animateY(3000)
        binding.barchart.xAxis.setDrawGridLines(false)
        binding.barchart.xAxis.setDrawAxisLine(false)
        binding.barchart.axisLeft.setDrawAxisLine(false)
        binding.barchart.axisLeft.setDrawGridLines(false)
        binding.barchart.axisRight.setDrawGridLines(false)
        binding.barchart.axisRight.isEnabled = false
        binding.barchart.legend.isEnabled = false
        binding.barchart.description = des
        binding.barchart.notifyDataSetChanged()
        binding.barchart.invalidate()
        binding.barchart.setTouchEnabled(false)
        binding.barchart.barData.barWidth = .5f


    }

    override fun onRefresh() {
        model.getDashboard()
    }

    private fun clearUserAndLogout() {
        model.clearDatastore()
        startActivity(Intent(context, FormActivity::class.java))
        requireActivity().finishAffinity()
    }

    fun makeAlarm() {
        //cancelAlarm()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReciever::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1000,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val cal: Calendar = Calendar.getInstance()

        cal.timeInMillis = System.currentTimeMillis()
        cal.clear()
        cal.set(2022, 8, 3, 15, 53, 30)

        alarmManager.setRepeating(
            AlarmManager.RTC,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

}