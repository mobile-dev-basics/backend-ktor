package com.mobile.email

import ch.qos.logback.classic.Logger
import com.mobile.clients.BrokerClient
import com.mobile.clients.RedisClient
import com.mobile.clients.RedisConfiguration
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis

fun main() {
    startKoin {
        slf4jLogger()
        modules(emailModule)
    }
    val emailService = EmailService()
    emailService.listenForWelcomeEmail(RedisConfiguration.WELCOME_EMAIL_CHANNEL)
}

val emailModule = module {
    single<Logger> { LoggerFactory.getLogger("logger") as Logger }
    single<Jedis> { Jedis(RedisConfiguration.HOST, RedisConfiguration.PORT) }
    singleOf(::RedisClient) { bind<BrokerClient>() }

}