package com.mobile.plugins

import ch.qos.logback.classic.Logger
import com.mobile.clients.BrokerClient
import com.mobile.clients.RedisClient
import com.mobile.clients.RedisConfiguration
import com.mobile.repository.TodoRepository
import com.mobile.repository.TodoRepositoryImpl
import com.mobile.repository.UserRepository
import com.mobile.repository.UserRepositoryImpl
import com.mobile.security.hashing.HashingService
import com.mobile.security.token.TokenService
import com.mobile.security.hashing.SHA256HashingService
import com.mobile.security.token.JwtTokenService
import com.mobile.services.TodoService
import com.mobile.services.UserService
import com.mobile.security.token.TokenConfig
import io.ktor.server.application.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.bind
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis

fun Application.configureKoin() {
    val logger = LoggerFactory.getLogger("logger ->")

    val todoModule = module {
        singleOf(::TodoRepositoryImpl) { bind<TodoRepository>() }
        singleOf(::TodoService)
    }
    val userModule = module {
        singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
        singleOf(::UserService)
    }
    val securityModule = module {
        singleOf(::SHA256HashingService) { bind<HashingService>() }
        singleOf(::JwtTokenService) { bind<TokenService>() }
    }
    val brokerModule = module {
        single<Jedis> { Jedis(RedisConfiguration.HOST, RedisConfiguration.PORT) }
        singleOf(::RedisClient) { bind<BrokerClient>() }
    }
    val loggerModule = module {
        single<Logger> { LoggerFactory.getLogger("logger") as Logger }
    }
    install(Koin) {
        slf4jLogger()
        modules(todoModule, userModule, securityModule, loggerModule, brokerModule)
        createEagerInstances()
    }
}

