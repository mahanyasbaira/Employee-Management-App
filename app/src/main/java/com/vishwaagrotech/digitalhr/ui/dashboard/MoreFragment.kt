package com.vishwaagrotech.digitalhr.ui.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentMoreBinding
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.*
import com.vishwaagrotech.digitalhr.ui.form.FormActivity
import com.vishwaagrotech.digitalhr.utils.Constant
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

@AndroidEntryPoint
class MoreFragment : Fragment() {
    lateinit var binding: FragmentMoreBinding

    private val model: MoreViewModel by viewModels()
    private val headerModel: HeaderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        binding.handler = this

        binding.layoutHeader.headerModel = headerModel
        binding.layoutHeader.lifecycleOwner = viewLifecycleOwner


        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.checkSecurity.setOnCheckedChangeListener { compoundButton, b ->
            Log.e("Check fingerprint", b.toString())
            if (b) {
                model.storeSecurityCheck(b)
            } else {
                checkFingerprint()
            }
        }

        checkSecurity()
        observeLogOut()
        headerModel.getUserDetail()
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

    private fun checkSecurity() {
        model.getSecurityCheck()
        model.securityCheck.observe(viewLifecycleOwner) {
            binding.checkSecurity.isChecked = it
        }
    }

    fun onNoticeClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_noticeFragment)
    }

    fun onChangePasswordClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_changePasswordFragment2)
    }

    fun onHolidayClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_holidayFragment)
    }

    fun onRulesClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_companyRulesFragment)
    }

    fun onProfileClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_profileFragment2)
    }

    fun onEmployeeClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_employeeFragment)
    }

    fun onLeaveCalendarClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_leaveCalendarFragment)
    }

    fun onMeetingClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_meetingFragment)
    }

    fun onTadaClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_tadaListFragment)
    }

    fun onSupportClicked() {
        findNavController().navigate(R.id.action_moreFragment_to_supportFragment)
    }

    fun onStaticPageClicked(slug: String) {
        findNavController().navigate(
            R.id.action_moreFragment_to_staticPageFragment,
            bundleOf("slug" to slug)
        )
    }

    fun onLogOutClicked() {
        LoadingUtils.showDialog(context, false)

        model.getLogoutResponse()
    }

    fun onPrivacyClicked() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("${Constant.APP_URL}privacy")
            )
        )
    }

    private fun observeLogOut() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.logoutResponse.collectLatest {
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

                            if (it.result != null) {
                                if (it.result.status_code == 200) {
                                    Toast.makeText(context, it.result.message, Toast.LENGTH_LONG)
                                        .show()
                                    clearUserAndLogout()
                                } else {
                                    Toast.makeText(context, it.result.message, Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        }
                    }
                }
            }
        }

        model.errorMessage.observe(viewLifecycleOwner) {
            LoadingUtils.hideDialog()
            if (it != null) {
                if (it.status_code == 401) {
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    clearUserAndLogout()
                }
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearUserAndLogout() {
        model.clearDatastore()
        startActivity(Intent(context, FormActivity::class.java))
        requireActivity().finishAffinity()
    }

    fun checkFingerprint() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor!!,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    biometricPrompt?.cancelAuthentication()
                    binding.checkSecurity.isChecked = true
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    model.storeSecurityCheck(false)
                    binding.checkSecurity.isChecked = false
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        context, "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    biometricPrompt?.cancelAuthentication()
                    binding.checkSecurity.isChecked = true
                }
            })

        val biometricManager = BiometricManager.from(requireContext())

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                promptFingerPrint()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "No Biometric found on the device", Toast.LENGTH_SHORT)
                    .show()
                biometricPrompt?.cancelAuthentication()
                model.storeSecurityCheck(false)
                binding.checkSecurity.isChecked = false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "No Biometric found on the device", Toast.LENGTH_SHORT)
                    .show()
                biometricPrompt?.cancelAuthentication()
                model.storeSecurityCheck(false)
                binding.checkSecurity.isChecked = false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(
                    context,
                    "No Biometric assign on the device. Assign biometric and try again",
                    Toast.LENGTH_SHORT
                )
                    .show()
                biometricPrompt?.cancelAuthentication()
                model.storeSecurityCheck(false)
                binding.checkSecurity.isChecked = false
            }
        }
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
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build()
            biometricPrompt?.authenticate(promptInfo)
        }
    }
}