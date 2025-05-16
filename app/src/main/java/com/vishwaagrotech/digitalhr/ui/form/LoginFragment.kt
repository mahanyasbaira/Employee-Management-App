package com.vishwaagrotech.digitalhr.ui.form

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentLoginBinding
import com.vishwaagrotech.digitalhr.ui.dashboard.DashboardActivity
import com.vishwaagrotech.digitalhr.ui.form.viewmodel.LoginViewModel
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.getStatusBarHeight
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    private val model: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.handler = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageLogo.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(0, activity?.getStatusBarHeight()!!.plus(10), 0, 0)
        }

        observeLogin()
        checkRememberMe()
    }

    private fun checkRememberMe() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            if (!model.rememberMe().trim().contentEquals("")) {
                binding.cbRememberMe.isChecked = true
            }
            binding.editEmail.setText(model.rememberMe())
        }
    }

    fun onForgetPasswordClicked() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("${Constant.APP_URL}password/reset")
            )
        )
    }

    fun onLoginClicked() {
        if (model.checkValidEmail(binding.editEmail.text.toString())) {
            if (model.checkPassword(binding.editPassword.text.toString())) {
                viewLifecycleOwner.lifecycleScope.launch {
                    model.loginUser(
                        binding.editEmail.text.toString(),
                        binding.editPassword.text.toString()
                    )
                }
            } else {
                binding.editPassword.error = "Password is empty/not valid"
            }
        } else {
            binding.editEmail.error = "Email is empty/not valid"
        }
    }

    private fun observeLogin() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loginResponse.collectLatest {
                when (it) {
                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        model.storeUserCacheDetail(it.result)

                        if (binding.cbRememberMe.isChecked) {
                            model.storeRememberMe(binding.editEmail.text.toString())
                        }else{
                            model.storeRememberMe("")
                        }

                        withContext(Dispatchers.Main) {
                            requireActivity().startActivity(
                                Intent(
                                    context,
                                    DashboardActivity::class.java
                                ).also { intent ->
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                })
                        }
                    }
                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(context, it.errorText, Toast.LENGTH_SHORT).show()
                    }
                    is EventHandler.Loading -> {
                        LoadingUtils.showDialog(context, false)
                    }
                    is EventHandler.Empty -> {}
                }
            }
        }
    }
}