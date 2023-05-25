package com.mobile.integration

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.mobile.plugins.*
import com.mobile.security.token.TokenConfig

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        val tokenConfig = TokenConfig(
            issuer = "http://0.0.0.0:8080",
            audience = "http://0.0.0.0:8080/api",
            expiresIn = 365L * 1000L * 68L * 24L,
            secret = "6A576E5A7234753778214125442A472D4A614E645267556B5870327335763879"
        )
        application {
            DatabaseFactory.init()
            configureSerialization()
            configureSecurity(tokenConfig)
            configureKoin()
            configureRouting(tokenConfig)
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
