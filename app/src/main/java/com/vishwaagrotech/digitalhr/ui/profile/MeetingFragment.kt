package com.vishwaagrotech.digitalhr.ui.profile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogAgendaBinding
import com.vishwaagrotech.digitalhr.databinding.DialogUserProfileBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentMeetingBinding
import com.vishwaagrotech.digitalhr.handler.MeetingHandler
import com.vishwaagrotech.digitalhr.model.Meeting
import com.vishwaagrotech.digitalhr.model.Participant
import com.vishwaagrotech.digitalhr.ui.profile.adapter.MeetingAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.TeamMeetingViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.PaginationScrollListener
import com.vishwaagrotech.digitalhr.utils.makeToast
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class MeetingFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, MeetingHandler {
    lateinit var binding: FragmentMeetingBinding
    private val model: TeamMeetingViewModel by viewModels()

    lateinit var meetingAdapter: MeetingAdapter

    private var meetingList = ArrayList<Meeting?>()

    private lateinit var layoutManager: LinearLayoutManager

    companion object {
        var PER_PAGE = 10
        var PAGE = 1
        var ISLOADING = false
        var ISLAST = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meeting, container, false)
        binding.handler = this

        toolbarConfig()

        PAGE = 1
        ISLOADING = false
        ISLAST = false
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeMeeting()
        observeMeeting()
        declarePagination()
        loadMeeting()

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Meetings"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    private fun makeMeeting() {
        meetingAdapter = MeetingAdapter(meetingList, this)
        binding.recyclerviewMeeting.apply {
            adapter = meetingAdapter
            layoutManager = this@MeetingFragment.layoutManager
        }
    }

    private fun loadMeeting() {
        ISLOADING = true
        model.fetchMeeting("", PER_PAGE, PAGE)
    }

    private fun declarePagination() {
        binding.recyclerviewMeeting.addOnScrollListener(object : PaginationScrollListener(
            layoutManager
        ) {
            override fun loadMoreItems() {
                meetingList.add(null)
                meetingAdapter.notifyItemInserted(meetingList.size - 1);
                loadMeeting()
            }

            override fun isLastPage(): Boolean {
                return ISLAST
            }

            override fun isLoading(): Boolean {
                return ISLOADING
            }
        })
    }

    private fun observeMeeting() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.teamMeetingResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                            }
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                if (it != null) {
                                    ISLOADING = false
                                    if (PAGE != 1) {
                                        meetingList.removeAt(meetingList.size - 1);
                                        val scrollPosition: Int = meetingList.size
                                        meetingAdapter.notifyItemRemoved(scrollPosition)
                                    }

                                    if (meetingList.size == 0) {
                                        binding.recyclerviewMeeting.visibility = View.GONE
                                        binding.lost.dialogLostConnection.visibility = View.VISIBLE
                                    }
                                }
                            }

                            requireActivity().makeToast(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(true)
                            }
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                            }
                            if (it.result != null) {
                                ISLOADING = false
                                binding.recyclerviewMeeting.visibility = View.VISIBLE
                                binding.lost.dialogLostConnection.visibility = View.GONE

                                if (PAGE != 1) {
                                    meetingList.removeAt(meetingList.size - 1);
                                    val scrollPosition: Int = meetingList.size
                                    meetingAdapter.notifyItemRemoved(scrollPosition)
                                }

                                if (it.result.size > 0) {
                                    if (PAGE == 1) {
                                        meetingList.clear()
                                    }
                                    meetingList.addAll(it.result)
                                    PAGE++
                                    meetingAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRefresh() {
        binding.refreshPage.setRefreshing(true)
        PAGE = 1;
        loadMeeting()
    }

    fun showAgenda(value: String) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding: DialogAgendaBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_agenda,
                null,
                false
            )
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.textAgenda.text = value

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    fun showParticipant(participant: Participant) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding: DialogUserProfileBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_user_profile,
                null,
                false
            )
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        sheetBinding.imageClose.setOnClickListener {
            dialog.dismiss()
        }

        sheetBinding.textUserFullname.text = participant.name

        Glide.with(requireContext()).load(participant.avatar).into(sheetBinding.imageProfile)

        sheetBinding.imageCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${participant.phone}"))
            startActivity(intent)
        }

        sheetBinding.imageEmail.setOnClickListener {
            try {
                val uriText = "mailto:${participant.email}"
                val uri = Uri.parse(uriText)
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.data = uri
                startActivity(Intent.createChooser(emailIntent, "Send email using..."))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "No email clients installed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    override fun onMeetingClicked(meeting: Meeting) {
        findNavController().navigate(
            R.id.action_meetingFragment_to_meetingDetailFragment,
            bundleOf("meeting_id" to meeting.id)
        )
    }

    override fun onProfileClicked(participant: Participant) {
        showParticipant(participant)
    }
}