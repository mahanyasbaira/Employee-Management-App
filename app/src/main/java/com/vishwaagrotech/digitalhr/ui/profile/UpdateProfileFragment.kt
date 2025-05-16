package com.vishwaagrotech.digitalhr.ui.profile

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentUpdateProfileBinding
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.UpdateProfileViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.*


@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {
    lateinit var binding: FragmentUpdateProfileBinding

    private val model: UpdateProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_update_profile, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateData()
        observeUpdateProfile()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Edit Profile"
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

    fun populateData() {
        val data = arguments!!

        if (data.containsKey("fullname")) {
            binding.editFullName.setText(data.getString("fullname"))
        }

        if (data.containsKey("email")) {
            binding.editEmail.setText(data.getString("email"))
        }

        if (data.containsKey("dob")) {
            binding.buttonDob.text = data.getString("dob")
        }

        if (data.containsKey("address")) {
            binding.editAddress.setText(data.getString("address"))
        }

        if (data.containsKey("gender")) {
            when (data.getString("gender").toString().trim().lowercase()) {
                "male" -> {
                    binding.rbMale.isChecked = true
                }
                "female" -> {
                    binding.rbFemale.isChecked = true
                }

                "other" -> {
                    binding.rbOthers.isChecked = true
                }
            }
        }

        if (data.containsKey("phone")) {
            binding.editPhone.setText(data.getString("phone"))
        }
    }

    fun onDatePickerClicked() {
        showDatePicker()
    }

    fun showDatePicker() {
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
            binding.buttonDob.text = "$year-${String.format("%02d",month.plus(1))}-${String.format("%02d",day)}"
        }
        datePickerDialog.show()
    }

    fun onUpdateClicked() {
        val fullname = binding.editFullName.text.toString()
        val email = binding.editEmail.text.toString()
        val address = binding.editAddress.text.toString()
        val phone = binding.editPhone.text.toString()
        val dob = binding.buttonDob.text.toString()

        val gender =
            if (binding.rbMale.isChecked) {
                "male"
            } else if (binding.rbFemale.isChecked) {
                "female"
            } else {
                "others"
            }


        if (!model.validateEmptyField(fullname)) {
            binding.editFullName.error = "Empty Field"
            return
        }

        if (!model.validateEmptyField(email)) {
            binding.editEmail.error = "Empty Field"
            return
        }

        if (!model.isEmailValid(email)) {
            binding.editEmail.error = "Email is not valid"
            return
        }

        if (!model.validateEmptyField(address)) {
            binding.editAddress.error = "Empty Field"
            return
        }

        if (!model.validateEmptyField(phone)) {
            binding.editPhone.error = "Empty Field"
            return
        }

        model.updateProfilePicture(fullname, email, address, phone, gender, dob)
    }

    private fun observeUpdateProfile() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.profileUpdate.collectLatest {
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
                            requireActivity().makeToast(it.result.message)
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

}