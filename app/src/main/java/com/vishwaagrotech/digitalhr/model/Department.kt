package com.vishwaagrotech.digitalhr.model

data class Department(val id: Int, val title: String){
    override fun toString(): String {
        return title
    }
}
