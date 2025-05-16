package com.vishwaagrotech.digitalhr.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentForgetPasswordBinding
import com.vishwaagrotech.digitalhr.utils.getStatusBarHeight

class ForgetPasswordFragment : Fragment() {
    lateinit var binding: FragmentForgetPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forget_password, container, false)
        binding.handler = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageLogo.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            setMargins(0, activity?.getStatusBarHeight()!!.plus(10), 0, 0)
        }

    }

    fun onChangePasswordClicked(){
        findNavController().navigate(R.id.action_forgetPasswordFragment_to_changePasswordFragment)
    }
}