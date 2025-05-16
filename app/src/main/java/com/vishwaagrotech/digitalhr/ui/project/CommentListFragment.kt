package com.vishwaagrotech.digitalhr.ui.project

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.DialogAddMentionBinding
import com.vishwaagrotech.digitalhr.databinding.FragmentCommentBinding
import com.vishwaagrotech.digitalhr.handler.CommentHandler
import com.vishwaagrotech.digitalhr.handler.MentionHandler
import com.vishwaagrotech.digitalhr.model.Comment
import com.vishwaagrotech.digitalhr.model.Mentioned
import com.vishwaagrotech.digitalhr.model.Reply
import com.vishwaagrotech.digitalhr.model.Team
import com.vishwaagrotech.digitalhr.ui.dashboard.viewmodel.HeaderViewModel
import com.vishwaagrotech.digitalhr.ui.project.adapter.AddMentionListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.CommentListAdapter
import com.vishwaagrotech.digitalhr.ui.project.adapter.MentionListAdapter
import com.vishwaagrotech.digitalhr.ui.project.viewmodel.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.simform.refresh.SSPullToRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class CommentListFragment : Fragment(), SSPullToRefreshLayout.OnRefreshListener, CommentHandler,
    CommentListAdapter.ReplyCallBack, MentionHandler {
    lateinit var binding: FragmentCommentBinding

    private val model: CommentViewModel by viewModels()
    private val headerModel: HeaderViewModel by viewModels()

    lateinit var commentListAdapter: CommentListAdapter
    var commentList: ArrayList<Comment> = ArrayList()

    lateinit var mentionListAdapter: MentionListAdapter
    var mentionList: ArrayList<Mentioned> = ArrayList()

    val args: CommentListFragmentArgs by navArgs()

    var taskTeams: ArrayList<Team> = ArrayList()

    companion object {
        var commentId = "";
        var reply = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comment, container, false)
        binding.handler = this
        binding.lifecycleOwner = viewLifecycleOwner
        binding.headerviewmodel = headerModel

        toolbarConfig()
        commentId = "";
        reply = false
        return binding.root
    }

    private fun toolbarConfig() {

        binding.toolbar.toolbar.title = "Comments"
        binding.toolbar.toolbar.setTitleTextColor(Color.WHITE)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar.toolbar)

        (requireActivity() as AppCompatActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
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

            android.R.id.home -> {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                super.onOptionsItemSelected(item)
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.refreshPage.setLottieAnimation("loader_full.json")
        //binding.refreshPage.setOnRefreshListener(this)

        observeCommentList()
        makeCommentAdapters()

        model.taskId = args.taskId
        taskTeams.clear()
        taskTeams.addAll(args.teams)
        model.getComments(model.taskId)

    }

    private fun observeCommentList() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loading.collectLatest {
                if (it) {

                } else {

                    Log.e("istriggered", true.toString())
                    commentList.clear()
                    commentList.addAll(model.commentList.value)
                    commentListAdapter.notifyDataSetChanged()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.loadingComment.collectLatest {
                if (it) {
                    binding.progressComment.visibility = View.VISIBLE
                } else {
                    binding.progressComment.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.commentList.collectLatest {
                commentList.clear()
                commentList.addAll(it)
                commentListAdapter.notifyDataSetChanged()
            }
        }

    }

    fun makeCommentAdapters() {
        commentListAdapter =
            CommentListAdapter(commentList, headerModel.person.value!!.id, this, this);
        binding.recyclerViewComments.apply {
            adapter = commentListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        mentionListAdapter =
            MentionListAdapter(mentionList, this);
        binding.recyclerViewMention.apply {
            adapter = mentionListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun onSendClicked() {
        if (binding.editDescription.text!!.isEmpty()) {
            return
        }
        model.commentId = commentId
        model.mentioned.clear()
        model.mentioned.addAll(mentionList)

        model.sendComment(model.taskId, model.commentId, binding.editDescription.text.toString())

        binding.editDescription.setText("")
        commentId = ""
        reply = false
        mentionList.clear()
        mentionListAdapter.notifyDataSetChanged()
        commentListAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {

    }

    override fun onCommentDeleteClicked(comment: Comment) {
        model.deleteComment(comment)
    }

    override fun onReplyDeleteClicked(reply: Reply) {
    }

    override fun onReplyCommentClicked(comment: Comment) {
        if (reply) {
            commentId = ""
            reply = false
            if (requireActivity().currentFocus != null) {
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    requireActivity().currentFocus!!.windowToken,
                    0
                )
            }
        } else {
            commentId = comment.id.toString()
            reply = true
            binding.editDescription.requestFocus()

            if (requireActivity().currentFocus != null) {
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(
                    requireActivity().currentFocus,
                    InputMethodManager.SHOW_IMPLICIT
                )
            }
        }

        val index = commentList.indexOf(comment)
        commentListAdapter.notifyItemChanged(index)
    }

    override fun onReplyDeleteCallBack(reply: Reply) {
        model.deleteReply(reply)
    }

    fun onLoadMore() {
        model.getComments(model.taskId)
    }

    lateinit var teamDialog: BottomSheetDialog
    fun onMentionAdded() {
        teamDialog = BottomSheetDialog(requireContext())

        val sheetBinding: DialogAddMentionBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.dialog_add_mention,
                null,
                false
            )

        sheetBinding.handler = this
        teamDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        sheetBinding.imageClose.setOnClickListener {
            teamDialog.dismiss()
        }

        val filteredTeams = ArrayList<Team>()
        for (team in taskTeams) {
            val value = mentionList.find {
                it.id == team.id.toString()
            }

            if (value == null) {
                filteredTeams.add(team)
            }
        }

        val teamAdapter = AddMentionListAdapter(filteredTeams, this);
        sheetBinding.recyclerViewTeams.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }


        teamDialog.setContentView(sheetBinding.root)
        teamDialog.show()
    }

    override fun onMentionRemoved(mentioned: Mentioned) {
        val person = mentionList.find {
            it.id == mentioned.id
        }

        mentionList.remove(person)

        mentionListAdapter.notifyDataSetChanged()
    }

    override fun onMentionAdded(team: Team) {
        mentionList.add(Mentioned(team.id.toString(),team.name,team.name))
        mentionListAdapter.notifyDataSetChanged()

        teamDialog.dismiss()
    }
}