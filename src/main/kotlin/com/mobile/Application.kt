package com.mobile

import com.mobile.plugins.DatabaseFactory
import com.mobile.plugins.configureKoin
import com.mobile.plugins.configureRouting
import com.mobile.plugins.configureSecurity
import com.mobile.plugins.configureSerialization
import com.mobile.security.token.TokenConfig
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

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
