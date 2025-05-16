package com.vishwaagrotech.digitalhr.ui.meetingdetail

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogAgendaBinding
import com.vishwaagrotech.digitalhr.databinding.DialogUserProfileBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentMeetingDetailBinding
import com.vishwaagrotech.digitalhr.handler.MeetingHandler
import com.vishwaagrotech.digitalhr.model.Meeting
import com.vishwaagrotech.digitalhr.model.Participant
import com.vishwaagrotech.digitalhr.ui.profile.adapter.ParticipantDetailAdapter
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.TeamMeetingViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
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
class MeetingDetailFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, MeetingHandler {
    lateinit var binding: FragmentMeetingDetailBinding
    private val model: TeamMeetingViewModel by viewModels()

    lateinit var participantAdapter: ParticipantDetailAdapter

    private var participantList = ArrayList<Participant>()

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_meeting_detail, container, false)
        binding.handler = this

        toolbarConfig()

        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeParticipantList()
        observeMeeting()
        loadMeeting("/".plus(arguments?.getInt("meeting_id")!!.toString()))

        binding.refreshPage.setLottieAnimation("loader_full.json")
        binding.refreshPage.setOnRefreshListener(this)
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "Meetings Detail"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        setHasOptionsMenu(true)
    }

    fun makeParticipantList() {
        participantAdapter = ParticipantDetailAdapter(participantList, this)
        binding.recyclerviewParticipants.apply {
            adapter = participantAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun loadMeeting(value: String) {
        model.fetchMeeting(value, 1, 1)
    }

    private fun observeMeeting() {
        viewLifecycleOwner.lifecycleScope.launch {
            model.teamMeetingResponse.collectLatest {
                when (it) {
                    is EventHandler.Empty -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                binding.layoutMeeting.visibility = View.GONE
                            }
                        }
                    }
                    is EventHandler.Failure -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                binding.layoutMeeting.visibility = View.GONE
                            }

                            requireActivity().makeToast(it.errorText)
                        }
                    }
                    is EventHandler.Loading -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(true)
                                binding.layoutMeeting.visibility = View.GONE
                            }
                        }
                    }
                    is EventHandler.Success -> {
                        withContext(Dispatchers.Main) {
                            binding.refreshPage.post {
                                binding.refreshPage.setRefreshing(false)
                                binding.layoutMeeting.visibility = View.VISIBLE
                            }
                            if (it.result != null) {
                                if (it.result.size > 0) {
                                    val meeting = it.result[0]

                                    with(Dispatchers.Main) {
                                        binding.textMeetingTitle.text = meeting.title
                                        binding.textMeetingVenue.text = meeting.venue
                                        binding.textMeetingDate.text =
                                            "Date - ${meeting.meeting_date} ${meeting.meeting_start_time}"
                                        binding.textMeetingAgenda.text = meeting.agenda

                                        Glide.with(requireContext()).load(meeting.image)
                                            .into(binding.imageMeetingImage)

                                        participantList.clear()
                                        participantList.addAll(meeting.participator)
                                        participantAdapter.notifyDataSetChanged()
                                    }
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
        loadMeeting("/".plus(arguments?.getInt("meeting_id")!!.toString()))
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

    }

    override fun onProfileClicked(participant: Participant) {

    }
}