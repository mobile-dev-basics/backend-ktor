package com.mobile.email

import ch.qos.logback.classic.Logger
import com.mobile.clients.BrokerClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import redis.clients.jedis.JedisPubSub
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailService : KoinComponent {

    private val logger by inject<Logger>()
    private val brokerClient by inject<BrokerClient>()
    val WELCOME_EMAIL_TEXT = "Hi! I'm using ToDo Application and my desire to jump out of the window has diminished. Join me"

    fun listenForWelcomeEmail(chanel: String) {
        try {
            val jedisPubSub = object : JedisPubSub() {
                override fun onMessage(channel: String?, message: String?) {
                    logger.info("Channel $channel has sent a message : $message")
                    if (message == null || !message.contains("@")) {
                        logger.warn("Incorrect email address: $message")
                    } else {
                        sendEmail(message)
                    }
                }

                override fun onSubscribe(channel: String?, subscribedChannels: Int) {
                    logger.info("Client is Subscribed to channel : $channel")
                    logger.info("Client is Subscribed to $subscribedChannels no. of channels")

                }

                override fun onUnsubscribe(channel: String?, subscribedChannels: Int) {
                    logger.info("Client is Unsubscribed from channel : $channel")
                    logger.info("Client is Subscribed to $subscribedChannels no. of channels")
                }
            }

            brokerClient.subscribe(jedisPubSub, chanel)

        } catch (e: Exception) {
            logger.error(e.message)
        }
    }

    fun sendEmail(email: String) {
        val username = EmailConfiguration.SENDER_EMAIL
        val password = EmailConfiguration.SENDER_EMAIL_PASS
        val subject = "ToDo Application"

        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", EmailConfiguration.SMTP_HOST)
            put("mail.smtp.port", EmailConfiguration.SMTP_PORT)
            put("mail.smtp.ssl.trust", "*")
            put("mail.smtp.ssl.protocols", "TLSv1.2")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(username, password)
            }
        })

        session.debug = true

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(username))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                setSubject(subject)
                setText(WELCOME_EMAIL_TEXT)
            }

            Transport.send(message)

            logger.info("Email to $email sent successfully")
        } catch (e: MessagingException) {
            println("Failed to send email to $email. Error message: ${e.message}")
        }
    }

}