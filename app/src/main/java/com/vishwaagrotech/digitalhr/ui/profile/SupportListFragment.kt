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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentSupportListBinding
import com.vishwaagrotech.digitalhr.handler.SupportHandler
import com.vishwaagrotech.digitalhr.model.Support
import com.vishwaagrotech.digitalhr.ui.profile.adapter.SupportListAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.SupportViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SupportListFragment : Fragment(), SupportHandler {
    lateinit var binding: FragmentSupportListBinding

    var supports: ArrayList<Support> = ArrayList()

    lateinit var supportAdapter: SupportListAdapter

    private val model: SupportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support_list, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSupports()
        makeSupport()

        model.getSupportList()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "My Tickets"
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

    fun makeSupport() {
        supportAdapter = SupportListAdapter(supports, this)
        binding.recyclerViewSupportList.apply {
            adapter = supportAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun observeSupports() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.supportList.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(context, false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        supports.clear()
                        supports.addAll(it.result)
                        supportAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onSupportClicked(support: Support) {
        val direction =
            SupportListFragmentDirections.actionSupportListFragmentToSupportDetailFragment(support)
        findNavController().navigate(direction)
    }
}