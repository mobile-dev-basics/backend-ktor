package com.mobile.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mobile.security.token.TokenConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity(config: TokenConfig) {
    install(Authentication) {
        jwt{
            realm = "Accept 'api'"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(config.secret))
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .build()
            )
            validate{credential ->
                if(credential.payload.audience.contains(config.audience)) JWTPrincipal(credential.payload) else null
            }
            challenge{defaultScheme, realm -> call.respond(HttpStatusCode.Unauthorized, "Token is not valid or expired") }
        }
    }
}