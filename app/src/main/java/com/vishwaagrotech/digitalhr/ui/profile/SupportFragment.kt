package com.vishwaagrotech.digitalhr.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentSupportBinding
import com.vishwaagrotech.digitalhr.model.Department
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.SupportViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SupportFragment : Fragment() {
    lateinit var binding: FragmentSupportBinding
    private val model: SupportViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_support, container, false)
        binding.handler = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSpinnerSelected()
        observeDepartment()
        observeSupportAdded()
        model.getSupportDepartments()

        binding.buttonSubmitLeave.setOnClickListener(View.OnClickListener {
            saveSupport()
        })
    }

    private fun saveSupport() {
        val title = binding.editTitle.text.toString()
        val description = binding.editDescription.text.toString()

        if (title.isEmpty()) {
            binding.editTitle.error = "Required Field"
            return
        }

        if (description.isEmpty()) {
            binding.editDescription.error = "Required Field"
            return
        }

        if (model.departmentId == 0) {
            Toast.makeText(context, "Department need to be selected", Toast.LENGTH_SHORT).show()
            return
        }

        model.saveSupport(title, description)
    }

    private fun observeDepartment() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.rDepartment.collectLatest {
                when (it) {
                    EventHandler.Empty -> {

                    }

                    is EventHandler.Failure -> {
                        Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {

                    }

                    is EventHandler.Success -> {
                        val adpater: ArrayAdapter<Department> = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            it.result
                        )

                        binding.spinnerDepartment.adapter = adpater
                    }
                }
            }
        }
    }

    private fun observeSupportAdded() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.rSaveSupport.collectLatest {
                when (it) {
                    EventHandler.Empty -> {

                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {

                        LoadingUtils.hideDialog()
                        Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(context, false)
                    }

                    is EventHandler.Success -> {

                        LoadingUtils.hideDialog()
                        findNavController().navigateUp()
                        requireActivity().showConfirmDialog("Support has been submitted")
                    }
                }
            }
        }
    }

    private fun onSpinnerSelected() {
        binding.spinnerDepartment.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {

                    val selectedObject = binding.spinnerDepartment.selectedItem as Department

                    model.departmentId = selectedObject.id

                }
            }
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Support"
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
        } else if (item.itemId == R.id.action_show_support) {
            findNavController().navigate(R.id.action_supportFragment_to_supportListFragment)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_support, menu)
    }
}