package com.mobile

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.mobile.plugins.*
import com.mobile.security.hashing.SHA256HashingService
import com.mobile.security.token.JwtTokenService
import com.mobile.security.token.TokenConfig

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val tokenConfig = TokenConfig(
        issuer = "http://0.0.0.0:8080",
        audience = "http://0.0.0.0:8080/api",
        expiresIn = 365L * 1000L * 68L * 24L,
        secret = "6A576E5A7234753778214125442A472D4A614E645267556B5870327335763879"
    )
    DatabaseFactory.init()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureKoin()
    configureRouting(tokenConfig)
}
