package com.mobile.clients

import redis.clients.jedis.BinaryJedisPubSub
import redis.clients.jedis.JedisPubSub

interface BrokerClient {

    fun subscribe(jedisPubSub: JedisPubSub, chanel: String)

    fun publish(message: String, chanel: String)
}