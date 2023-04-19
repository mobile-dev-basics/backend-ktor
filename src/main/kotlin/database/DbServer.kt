package database

import redis.RedisClient
import redis.RedisConfiguration

fun main() {
    val redisClient = RedisClient(RedisConfiguration.HOST, RedisConfiguration.PORT)
    redisClient.testSub()
}