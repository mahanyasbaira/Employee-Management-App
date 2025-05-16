package com.vishwaagrotech.digitalhr.repository.mapper

import com.vishwaagrotech.digitalhr.model.CompanyRule
import com.vishwaagrotech.digitalhr.repository.network.api.companyrules.CompanyRules

object CompanyRulesMapper {

    fun mapToEntity(rules: CompanyRules): CompanyRule {
        return CompanyRule(
            rules.content_type,
            rules.description,
            rules.title,
        )
    }

    fun mapToEntityList(entities: ArrayList<CompanyRules>): List<CompanyRule> {
        return entities.map {
            mapToEntity(it)
        }
    }
}