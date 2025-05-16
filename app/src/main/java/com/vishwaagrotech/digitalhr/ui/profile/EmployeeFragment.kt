package com.vishwaagrotech.digitalhr.ui.profile
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentEmployeeBinding
import com.vishwaagrotech.digitalhr.repository.network.api.teamsheet.Employee
import com.vishwaagrotech.digitalhr.ui.profile.adapter.EmployeeAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.TeamSheetViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.makeToast
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class EmployeeFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener,
    EmployeeAdapter.onEmployeeClicked {
    lateinit var binding: FragmentEmployeeBinding

    private val model: TeamSheetViewModel by viewModels()

    lateinit var employeeAdapter: EmployeeAdapter

    private val employeeList: ArrayList<Employee> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_employee, container, false)
        binding.handler = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeTeam()
        makeEmployee()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)

        onRefresh()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = ""
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

    private fun makeEmployee() {
        employeeAdapter = EmployeeAdapter(employeeList,this)
        binding.recyclerviewEmployee.apply {
            adapter = employeeAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }


    private fun observeTeam() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.teamSheetResponse.collectLatest {
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
                        employeeList.clear()
                        employeeList.addAll(it.result.data.employee)

                        binding.toolbar.toolbar.title = it.result.data.name

                        withContext(Dispatchers.Main){
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                employeeAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        model.getTeamSheetResponse()
    }

    override fun onCallClicked(employee: Employee) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:${employee.phone}")
        startActivity(intent)
    }

    override fun onMessageClicked(employee: Employee) {
        val uri = Uri.parse("smsto:${employee.phone}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        startActivity(intent)
    }

    override fun onProfileClicked(employee: Employee) {
        val bundle = Bundle()
        bundle.putString("userId", employee.id.toString())
        findNavController(view!!).navigate(R.id.action_employeeFragment_to_employeeDetailFragment, bundle)
    }
}