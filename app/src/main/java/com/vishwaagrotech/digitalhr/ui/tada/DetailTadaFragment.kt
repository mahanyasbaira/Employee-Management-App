package com.vishwaagrotech.digitalhr.ui.tada

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentDetailTadaBinding
import com.vishwaagrotech.digitalhr.handler.AttachmentHandler
import com.vishwaagrotech.digitalhr.model.Attachment
import com.vishwaagrotech.digitalhr.ui.tada.adapter.ServerAttachmentListAdapter
import com.vishwaagrotech.digitalhr.ui.tada.viewmodel.EditTadaViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DetailTadaFragment : Fragment(), AttachmentHandler {
    lateinit var binding: FragmentDetailTadaBinding

    val model: EditTadaViewModel by viewModels()

    lateinit var oldAttachmentListAdapter: ServerAttachmentListAdapter

    private var oldAttachments: ArrayList<Attachment> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_detail_tada, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model.onTadaAttachmentDetail(arguments!!.getString("tadaId", "0"))
        makeLocalAttachment()
        observeTadaDetail()
        observeAttachments()
    }

    private fun makeLocalAttachment() {
        oldAttachmentListAdapter = ServerAttachmentListAdapter(oldAttachments, this)
        binding.recyclerViewOldAttachmentList.apply {
            adapter = oldAttachmentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun observeAttachments() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            withContext(Dispatchers.Main) {
                model.oldAttachmentList.collect {
                    oldAttachments.clear()
                    oldAttachments.addAll(it)
                    oldAttachmentListAdapter.notifyDataSetChanged()
                }
            }
        }
    }


    fun observeTadaDetail() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.tada.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                        Toast.makeText(requireContext(), it.errorText, Toast.LENGTH_SHORT).show()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(requireContext(), false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        binding.textTitle.text = it.result.title
                        binding.textDescription.text =
                            Html.fromHtml(it.result.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                        binding.textVerifiedBy.text = it.result.verified_by
                        binding.textDate.text = it.result.submitted_date
                        binding.textExpense.text = it.result.total_expense
                    }
                }
            }
        }
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "TADA Detail"
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

    override fun onAttachmentClicked(attachment: Attachment) {
    }

    override fun onRemoveAttachmentClicked(attachment: Attachment) {
        Toast.makeText(
            requireContext(),
            "Can not perform action from detail page. Please edit and delete the attachment.",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onLoadAttachmentClicked(attachment: Attachment) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(attachment.url))
        startActivity(browserIntent)
    }
}