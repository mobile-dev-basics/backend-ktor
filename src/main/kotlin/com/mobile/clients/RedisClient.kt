package com.mobile.clients

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

class RedisClient : BrokerClient, KoinComponent {

    private val jedis by inject<Jedis>()
    override fun subscribe(jedisPubSub: JedisPubSub, chanel: String) {
        jedis.subscribe(jedisPubSub, chanel)
    }

    override fun publish(message: String, chanel: String) {
        try {
            jedis.publish(chanel, message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}