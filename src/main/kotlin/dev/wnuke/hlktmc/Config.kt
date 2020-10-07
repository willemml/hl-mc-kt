package dev.wnuke.hlktmc

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class ChatBotConfig(
    val username: String = randomAlphanumeric(10),
    val password: String = "",
    val server: String = "127.0.0.1",
    val port: Int = 25565,
    val prefix: String = "!",
    val connectionLogs: Boolean = false,
    val chatLogs: Boolean = false
)

@Serializable
class DiscordConfig(val token: String = "", val prefix: String = "!")

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"