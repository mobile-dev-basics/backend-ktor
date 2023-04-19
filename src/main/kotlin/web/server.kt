package web

import di.redisModule
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*
import org.koin.core.context.startKoin
import org.koin.ktor.ext.inject
import redis.RedisClient
import redis.RedisConfiguration


fun main() {
//    startKoin{
//        redisModule
//    }
    val redisClient = RedisClient(RedisConfiguration.HOST, RedisConfiguration.PORT)

    embeddedServer(Tomcat, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondText("Hello Ktor!", ContentType.Text.Plain)
            }
            post("/test/redis") {
                val text = call.receiveText()
                redisClient.testPub(text)
                call.respondText("Pub", ContentType.Text.Plain)
            }
        }
    }.start(wait = true)
}