package com.mobile.dto.responses

import com.mobile.models.Type
import java.time.LocalDate

data class TodoResponse(val name: String, val creationDate: LocalDate,
                        val priority: Int, val description: String?, val type: Type
)