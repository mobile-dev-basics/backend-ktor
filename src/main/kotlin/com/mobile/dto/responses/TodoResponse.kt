package com.mobile.dto.responses

import java.time.LocalDate

data class TodoResponse(
    val id : Long,
    val title : String,
    val description: String,
    val priority: String,
    var creationDate: Long,
    val endDate: Long
)