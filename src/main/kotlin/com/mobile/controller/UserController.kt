package com.mobile.controller

import ch.qos.logback.classic.Logger
import com.google.gson.Gson
import com.mobile.clients.BrokerClient
import com.mobile.clients.RedisConfiguration
import com.mobile.dto.requests.LoginCredentials
import com.mobile.dto.requests.RegisterCredentials
import com.mobile.dto.responses.AuthResponse
import com.mobile.dto.responses.ErrorResponse
import com.mobile.dto.responses.UserResponse
import com.mobile.models.User
import com.mobile.security.hashing.HashingService
import com.mobile.security.hashing.SaltedHash
import com.mobile.security.token.TokenClaim
import com.mobile.security.token.TokenConfig
import com.mobile.security.token.TokenService
import com.mobile.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject


fun Route.userRouting(tokenConfig: TokenConfig) {

    // val todoService by inject<TodoService>()
    val userService by inject<UserService>()
    val hashingService by inject<HashingService>()
    val tokenService by inject<TokenService>()
    val brokerClient by inject<BrokerClient>()
    val logger by inject<Logger>()

    route("/api") {

        post("/login") {
            logger.info("Got request to log in!")
            val request = kotlin.runCatching { call.receiveNullable<LoginCredentials>() }.getOrNull() ?: kotlin.run {
                logger.info("Bad request could not map to LoginCredentials!")
                call.respond(HttpStatusCode.BadRequest, ErrorResponse(HttpStatusCode.BadRequest.value, "Bad Request"))
                return@post
            }
            val user = userService.getUserByEmail(email = request.email)
            if (user == null) {
                logger.info("Incorrect username or password!")
                println("The user is null")
                call.respond(HttpStatusCode.Conflict, ErrorResponse(HttpStatusCode.Conflict.value, "Incorrect username or password"))
                return@post
            }

            val isValidPassword = hashingService.verify(
                value = request.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )
            if (!isValidPassword) {
                logger.info("The password could not be verified")
                call.respond(HttpStatusCode.Conflict, ErrorResponse(HttpStatusCode.Conflict.value, "Incorrect username or password"))
                return@post
            }
            val token = tokenService.generate(
                config = tokenConfig,
                TokenClaim(
                    name = "userId",
                    value = user.id.toString()
                )
            )

            call.respond(HttpStatusCode.OK, message = AuthResponse(token = token))
            logger.info("Successfully logged in!")

        }
        post("/register") {
            logger.info("Received a request to register a user")
            val request = kotlin.runCatching { call.receiveNullable<RegisterCredentials>() }.getOrNull() ?: kotlin.run {
                logger.info("Could not map request to RegisterCredentials()")
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val areFieldBlank = request.email.isBlank() || request.password.isBlank()
            val isPasswordShort = request.password.length < 4

            if (userService.getUserByEmail(request.email) != null) {
                logger.info("User already exists!")
                call.respond(HttpStatusCode.Conflict, "Email is already registered!")
                return@post
            }

            if (areFieldBlank || isPasswordShort) {
                logger.info("Bad request! Fields are blank or password is too short")
                call.respond(HttpStatusCode.Conflict, "Bad request")
                return@post
            }

            val saltedHash = hashingService.generateSaltedHash(request.password)
            val wasAcknowledged = userService.register(request.name, request.email, saltedHash.hash, saltedHash.salt)
            if (wasAcknowledged == null) {
                logger.info("Database failed to register the user!")
                call.respond(HttpStatusCode.Conflict, "Failed to register")
                return@post
            } else {
                val token = tokenService.generate(
                    config = tokenConfig,
                    TokenClaim(
                        name = "userId",
                        value = wasAcknowledged.id.toString()
                    )
                )
                call.respond(HttpStatusCode.OK, message = AuthResponse(token))
                logger.info("Successfully registered the user")
            }
        }
        post("/invite") {
            logger.info("Received a request to send invite email")
            val email = call.request.queryParameters["email"]
            if (email == null) {
                call.respond(HttpStatusCode.BadRequest, "Email required")
                return@post
            }
            brokerClient.publish(email, RedisConfiguration.WELCOME_EMAIL_CHANNEL)
            call.respond(HttpStatusCode.OK)
            logger.info("Message published to broker successfully")
        }
    }
}

fun Route.userInfo() {
    val userService by inject<UserService>()
    val logger by inject<Logger>()
    authenticate {
        get("/api/info") {
            logger.info("Got request to fetch user info!")
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", Long::class)
            if (userId == null) {
                logger.warn("User id was null!")
                call.respond(HttpStatusCode.Conflict, ErrorResponse(HttpStatusCode.Conflict.value, "User not found!"))
                return@get
            }
            val user: User? = userService.findById(userId)
            if (user == null) {
                logger.warn("User was not found!")
                call.respond(HttpStatusCode.NotFound, ErrorResponse(HttpStatusCode.NotFound.value, "User not found!"))
                return@get
            }

            call.respond(HttpStatusCode.OK, UserResponse(name = user.name, email = user.email))
            logger.info("Successfully responded to get request")
        }
    }
}

