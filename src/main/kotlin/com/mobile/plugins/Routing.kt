package com.mobile.plugins

import com.mobile.controller.todoRouting
import com.mobile.controller.userInfo
import com.mobile.controller.userRouting
import com.mobile.repository.UserRepositoryImpl
import com.mobile.security.token.TokenConfig
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.koin.ktor.ext.inject

fun Application.configureRouting(tokenConfig: TokenConfig) {
    val userRepository by inject<UserRepositoryImpl>()
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
    routing {
        todoRouting()
        userRouting(tokenConfig)
        userInfo()
        get("/metrics") {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}
