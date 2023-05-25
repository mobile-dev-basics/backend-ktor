package com.mobile

import com.mobile.dto.requests.LoginCredentials
import com.mobile.dto.responses.AuthResponse
import com.mobile.plugins.*
import com.mobile.security.token.TokenConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TodoControllerTest {
    @Test
    fun getToDoTest() = testApplication {
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
        val httpResponse: HttpResponse = client.post("/api/login") {
            setBody(LoginCredentials("test@gmail.com", "test"))
        }
        val authResponse = httpResponse.body<AuthResponse>()
        client.get("/api/todo/1") {
            headers {
                append(HttpHeaders.Authorization, "Bearer ${authResponse.token}")
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Successfully responded to get request!", bodyAsText())
        }
    }
}
