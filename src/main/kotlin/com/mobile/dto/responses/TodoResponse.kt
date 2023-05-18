package com.mobile.dto.responses

import com.mobile.models.Type
import java.time.LocalDate

data class TodoResponse(
    val id : Long,
    val title : String,
    val description: String,
    val endDate: LocalDate,
    val priority: String
)