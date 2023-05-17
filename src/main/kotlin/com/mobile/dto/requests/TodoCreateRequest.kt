package com.mobile.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class TodoCreateRequest
    (val name: String,
    val description: String,
    val priority: String,
    val date: Long)
