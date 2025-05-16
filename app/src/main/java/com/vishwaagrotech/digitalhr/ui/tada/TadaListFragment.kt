package com.vishwaagrotech.digitalhr.ui.tada

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.vishwaagrotech.digitalhr.R
import com.vishwaagrotech.digitalhr.databinding.FragmentTadaListingBinding
import com.vishwaagrotech.digitalhr.handler.TadaHandler
import com.vishwaagrotech.digitalhr.model.Tada
import com.vishwaagrotech.digitalhr.ui.tada.adapter.TadaListAdapter
import com.vishwaagrotech.digitalhr.ui.tada.viewmodel.TadaListViewModel
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.vishwaagrotech.digitalhr.utils.LoadingUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class TadaListFragment : Fragment(), TadaHandler {
    lateinit var binding: FragmentTadaListingBinding

    val model: TadaListViewModel by viewModels()

    lateinit var tadaAdapter: TadaListAdapter

    private var tadas = ArrayList<Tada>()

    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_tada_listing, container, false)
        binding.handler = this
        binding.lifecycleOwner = this

        toolbarConfig()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeTadaList()
        observeTada()

        model.onTadaListFetch()

        binding.floatCreateTada.setOnClickListener {
            findNavController().navigate(R.id.action_tadaListFragment_to_createTadaFragment)
        }
    }

    private fun toolbarConfig() {
        binding.toolbar.toolbar.title = "My TADA(s)"
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

    fun makeTadaList() {
        tadaAdapter = TadaListAdapter(tadas, this)
        binding.recyclerViewTadaList.apply {
            adapter = tadaAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun observeTada() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            model.tadaList.collectLatest {
                when (it) {
                    EventHandler.Empty -> {
                        LoadingUtils.hideDialog()
                    }

                    is EventHandler.Failure -> {
                        LoadingUtils.hideDialog()
                    }

                    EventHandler.Loading -> {
                        LoadingUtils.showDialog(requireContext(), false)
                    }

                    is EventHandler.Success -> {
                        LoadingUtils.hideDialog()
                        tadas.clear()
                        tadas.addAll(it.result)
                        tadaAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onTadaClicked(tada: Tada) {
        val bundle = Bundle()
        bundle.putString("tadaId", tada.id.toString())
        findNavController().navigate(
            R.id.action_tadaListFragment_to_detailTadaFragment,
            bundle
        )
    }

    override fun onEditTadaClicked(tada: Tada) {
        if (tada.status == "Accepted") {
            Toast.makeText(requireContext(), "Approved TADA can't be edited", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val bundle = Bundle()
        bundle.putString("tadaId", tada.id.toString())
        findNavController().navigate(
            R.id.action_tadaListFragment_to_editTadaFragment,
            bundle
        )
    }
}