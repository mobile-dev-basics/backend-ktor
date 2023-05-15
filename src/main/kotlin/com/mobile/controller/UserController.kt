package com.mobile.controller

import com.mobile.dto.requests.LoginCredentials
import com.mobile.dto.requests.RegisterCredentials
import com.mobile.dto.responses.AuthResponse
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


fun Route.userRouting(tokenConfig: TokenConfig){

    // val todoService by inject<TodoService>()
    val userService by inject<UserService>()
    val hashingService by inject<HashingService> ()
    val tokenService by inject<TokenService> ()

    route("/api"){
        post("/login"){
            val request = kotlin.runCatching { call.receiveNullable<LoginCredentials>() }.getOrNull() ?:
                kotlin.run{
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
            val user = userService.getUserByEmail(email = request.email)
            if (user == null){
                println("The user is null")
                call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
                return@post
            }

            val isValidPassword = hashingService.verify(
                value = request.password,
                saltedHash = SaltedHash(
                    hash = user.password,
                    salt = user.salt
                )
            )
            if (!isValidPassword){
                println("The password is invalid!")
                call.respond(HttpStatusCode.Conflict, "Incorrect username or password")
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

        }
        post("/register"){
            val request = kotlin.runCatching { call.receiveNullable<RegisterCredentials>() }.getOrNull() ?:
                            kotlin.run {
                                call.respond(HttpStatusCode.BadRequest)
                                return@post
                            }
            val areFieldBlank = request.email.isBlank() || request.password.isBlank()
            val isPasswordShort = request.password.length < 4

            if (userService.getUserByEmail(request.email) != null){
                call.respond(HttpStatusCode.Conflict, "Email is already registered!")
                return@post
            }

            if(areFieldBlank || isPasswordShort){
                call.respond(HttpStatusCode.Conflict, "Bad request")
                return@post
            }

            val saltedHash = hashingService.generateSaltedHash(request.password)
            val wasAcknowledged = userService.register(request.name, request.email, saltedHash.hash, saltedHash.salt)
            if (wasAcknowledged == null){
                call.respond(HttpStatusCode.Conflict, "Failed to register")
                return@post
            }
            else{
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}

fun Route.secretInfo(){
    val userService by inject<UserService>()
    authenticate {
        get("/secret"){
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            call.respond(HttpStatusCode.OK, "Your id: $userId")
        }
    }

}