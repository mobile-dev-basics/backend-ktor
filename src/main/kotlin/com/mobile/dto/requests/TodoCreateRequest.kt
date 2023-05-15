package com.mobile.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class TodoCreateRequest(val name: String)
