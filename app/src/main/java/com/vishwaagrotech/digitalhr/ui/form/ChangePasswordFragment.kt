package com.vishwaagrotech.digitalhr.ui.form

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.*
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.ChangePasswordViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    lateinit var binding: FragmentChangePasswordBinding

    private val model: ChangePasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        binding.handler = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarConfig()
        observeChangePassword()
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Change Password"
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

    fun onChangePasswordClicked() {
        val old = binding.editOldPassword.text.toString()
        val new = binding.editNewPassword.text.toString()
        val confirm = binding.editConfirmPassword.text.toString()

        val validate1 = model.validateEmptyField(old)
        val validate2 = model.validateEmptyField(new)
        val validate3 = model.validateEmptyField(confirm)
        val validate4 = model.validateNewPasswordField(new, confirm)

        if (!validate1.contentEquals("")) {
            binding.inputOldPassword.error = validate1
            return
        }

        if (!validate2.contentEquals("")) {
            binding.inputNewPassword.error = validate2
            return
        }

        if (!validate3.contentEquals("")) {
            binding.inputConfirmPassword.error = validate3
            return
        }

        if (!validate4.contentEquals("")) {
            binding.inputConfirmPassword.error = validate4
            return
        }

        model.getChangePasswordResponse(old, new, confirm)
    }

    private fun observeChangePassword() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.changePassword.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            LoadingUtils.hideDialog()
                            Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(context, it.result.message, Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }
}