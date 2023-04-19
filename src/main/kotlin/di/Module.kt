package di

import org.koin.dsl.module
import redis.RedisClient
import redis.RedisConfiguration

val redisModule = module {
    single { RedisClient(RedisConfiguration.HOST, RedisConfiguration.PORT) }
}

