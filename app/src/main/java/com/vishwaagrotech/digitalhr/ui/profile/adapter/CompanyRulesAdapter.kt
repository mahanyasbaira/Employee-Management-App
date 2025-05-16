package com.vishwaagrotech.digitalhr.ui.profile.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vishwaagrotech.digitalhr.databinding.RowCompanyRulesBinding
import com.vishwaagrotech.digitalhr.model.CompanyRule

class CompanyRulesAdapter(
    private val data: ArrayList<CompanyRule>
) :
    RecyclerView.Adapter<CompanyRulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompanyRulesAdapter.ViewHolder {
        val binding =
            RowCompanyRulesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.handler = this
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CompanyRulesAdapter.ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: RowCompanyRulesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(companyRule: CompanyRule) {
            binding.companyRule = companyRule

            val title = Html.fromHtml(companyRule.title, Html.FROM_HTML_MODE_COMPACT).toString()
            val desc =
                Html.fromHtml(companyRule.description, Html.FROM_HTML_MODE_COMPACT).toString()

            binding.dropdownTextView.setContentText(desc)
            binding.dropdownTextView.setTitleText(title)
        }
    }
}