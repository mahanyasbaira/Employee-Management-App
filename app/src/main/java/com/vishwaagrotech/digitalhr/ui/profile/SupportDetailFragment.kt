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
import androidx.navigation.fragment.navArgs
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentSupportDetailBinding
import com.vishwaagrotech.digitalhr.model.Support
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportDetailFragment : Fragment() {

    lateinit var binding: FragmentSupportDetailBinding

    private val args: SupportDetailFragmentArgs by navArgs()

    lateinit var support: Support;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        support = args.supportDetail
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_support_detail, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Ticket Detail"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeSupport()
    }

    private fun makeSupport(){
        binding.textAssignedTo.text = support.issueAt
        binding.textDate.text = support.createdAt
        binding.textDescription.text = support.description
        binding.textStatus.text = support.status
        binding.textSolvedBy.text = support.approvedBy.ifEmpty { "-" }
        binding.textSolvedBy.text = support.approvedAt.ifEmpty { "-" }

        if(support.status == "Pending"){
            binding.textStatus.setTextColor(binding.root.context.getColor(R.color.dark_orange))
        }else if(support.status == "In Progress"){
            binding.textStatus.setTextColor(binding.root.context.getColor(R.color.red_end))
        }else{
            binding.textStatus.setTextColor(binding.root.context.getColor(R.color.green_end))
        }
    }
}