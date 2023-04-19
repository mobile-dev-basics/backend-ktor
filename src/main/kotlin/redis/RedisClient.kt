package redis

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

class RedisClient(host: String, port: Int) {

    private val jedis = Jedis(host, port)

    fun testSub() {
        try {
            val jedisPubSub = object : JedisPubSub() {
                override fun onMessage(channel: String?, message: String?) {
                    println("Channel $channel has sent a message : $message")
                }

                override fun onSubscribe(channel: String?, subscribedChannels: Int) {
                    println("Client is Subscribed to channel : $channel")
                    println("Client is Subscribed to $subscribedChannels no. of channels")

                }

                override fun onUnsubscribe(channel: String?, subscribedChannels: Int) {
                    println("Client is Unsubscribed from channel : $channel");
                    println("Client is Subscribed to $subscribedChannels no. of channels");
                }
            }
            jedis.subscribe(jedisPubSub, "C1", "C2")

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun testPub(msg: String) {
        try {
            jedis.publish(if ((0 until 2).random() == 1) "C1" else "C2", msg)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}