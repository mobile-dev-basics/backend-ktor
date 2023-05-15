package com.mobile.security.hashing

data class SaltedHash (
    val hash: String,
    val salt: String
)
