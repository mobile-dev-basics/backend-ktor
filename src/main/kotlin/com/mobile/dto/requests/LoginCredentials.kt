package com.mobile.dto.requests

import kotlinx.serialization.Serializable

@Serializable
data class LoginCredentials(val email:String, val password: String)