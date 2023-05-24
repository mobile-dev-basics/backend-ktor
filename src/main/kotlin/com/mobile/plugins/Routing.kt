package com.mobile.plugins

import com.mobile.controller.todoRouting
import com.mobile.controller.userInfo
import com.mobile.controller.userRouting
import com.mobile.repository.UserRepositoryImpl
import com.mobile.security.token.TokenConfig
import io.ktor.server.routing.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {
    val userRepository by inject<UserRepositoryImpl>()
    routing {
        todoRouting()
        userRouting(tokenConfig)
        userInfo()
    }
}
