package com.vishwaagrotech.digitalhr.repository.network.api.companyrules

data class CompanyRulesResponse(
    val `data`: ArrayList<CompanyRules>,
    val message: String,
    val status: Boolean,
    val status_code: Int
)