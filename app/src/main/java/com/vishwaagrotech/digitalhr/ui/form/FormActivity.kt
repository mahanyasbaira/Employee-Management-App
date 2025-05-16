package com.vishwaagrotech.digitalhr.ui.form

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.ActivityFormBinding
import com.vishwaagrotech.digitalhr.utils.Statusbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormActivity : AppCompatActivity() {
    lateinit var binding: ActivityFormBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_form)
        binding.lifecycleOwner = this

        Statusbar.setStatusbarTheme(this, window, 0, binding.root)

        makeNavHost()
    }

    private fun makeNavHost() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.dashboard_nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

}