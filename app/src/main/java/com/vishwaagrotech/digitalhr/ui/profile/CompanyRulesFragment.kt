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
import com.vishwaagrotech.digitalhr.databinding.FragmentCompanyRulesBinding
import com.vishwaagrotech.digitalhr.model.CompanyRule
import com.vishwaagrotech.digitalhr.repository.mapper.CompanyRulesMapper
import com.vishwaagrotech.digitalhr.ui.profile.adapter.CompanyRulesAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.CompanyRulesViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class CompanyRulesFragment : Fragment() {
    lateinit var binding: FragmentCompanyRulesBinding

    private val model: CompanyRulesViewModel by viewModels()

    private var companyRules = ArrayList<CompanyRule>()
    lateinit var companyRulesAdapter: CompanyRulesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_company_rules, container, false)
        binding.handler = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeRules()
        makeCompanyRules()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Company Rules"
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

    private fun makeCompanyRules() {
        companyRulesAdapter = CompanyRulesAdapter(companyRules)
        binding.recyclerviewRules.apply {
            this.adapter = companyRulesAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observeRules() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.companyRulesResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            requireActivity().makeToast(it.errorText)
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

                            val rules = it.result.data
                            companyRules.clear()
                            companyRules.addAll(CompanyRulesMapper.mapToEntityList(rules))
                            companyRulesAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }
}