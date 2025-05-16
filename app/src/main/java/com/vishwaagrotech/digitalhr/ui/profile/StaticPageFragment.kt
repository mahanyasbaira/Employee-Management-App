package com.vishwaagrotech.digitalhr.ui.profile

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentStaticPageBinding
import com.vishwaagrotech.digitalhr.ui.profile.viewmodel.StaticPageViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import com.vishwaagrotech.digitalhr.utils.makeToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StaticPageFragment : Fragment() {
    lateinit var binding: FragmentStaticPageBinding

    private val model: StaticPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_static_page, container, false)
        binding.handler = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeStaticPageResponse()
        if (arguments != null) {
            val arg = arguments!!.getString("slug")
            if (arg != null) {
                getPage(arg)
            }
        }

    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = ""
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

    fun getPage(value: String) {
        model.getStaticPageResponse(value)
    }

    private fun observeStaticPageResponse() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.staticPageResponse.collectLatest {
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

                            val data = it.result.data

                            binding.textValue.text =
                                Html.fromHtml(data.description, Html.FROM_HTML_MODE_COMPACT)
                            binding.toolbar.toolbar.title = data.title
                        }
                    }
                }
            }
        }
    }

}