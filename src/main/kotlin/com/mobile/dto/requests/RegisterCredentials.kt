package com.mobile.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class RegisterCredentials(val name: String, val email: String, val password : String)