package com.vishwaagrotech.digitalhr.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.ActivityDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.lifecycleOwner = this

        makeDashboard()
    }

    private fun makeDashboard() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val popupMenu = PopupMenu(this, binding.root)
        popupMenu.inflate(R.menu.menu_dashboard)
        binding.navigationDashboard.setupWithNavController(popupMenu.menu, navController)

        navController.addOnDestinationChangedListener(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if (intent.hasExtra("type")) {
                if (intent.getStringExtra("type").contentEquals("notification")) {
                    navController.navigate(R.id.notificationFragment2)
                } else if (intent.getStringExtra("type").contentEquals("notice")) {
                    navController.navigate(R.id.noticeFragment2)
                } else if (intent.getStringExtra("type").contentEquals("meeting")) {
                    navController.navigate(
                        R.id.meetingDetailFragment2,
                        bundleOf("meeting_id" to intent.getIntExtra("id", 0))
                    )
                } else if (intent.getStringExtra("type").contentEquals("leave")) {
                    val item =
                        popupMenu.menu.findItem(R.id.leave_nav_graph)
                    NavigationUI.onNavDestinationSelected(item, navController)
                }
            }
        }, 1000)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment,
            R.id.leaveFragment,
            R.id.projectFragment,
            R.id.attendanceFragment,
            R.id.moreFragment -> {
                binding.navigationDashboard.visibility = View.VISIBLE
            }
            else -> {
                hideBottomBav()
            }
        }
    }

    private fun hideBottomBav() {
        binding.navigationDashboard.visibility = View.GONE
    }

    fun statusbarIconDark(boolean: Boolean) {
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
            boolean
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navController.handleDeepLink(intent)
    }
}