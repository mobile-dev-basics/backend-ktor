package com.mobile.dto.responses

import io.ktor.http.*

data class ErrorResponse(
    val code: Int,
    val errorMessage: String
)
