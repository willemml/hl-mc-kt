package dev.wnuke.hlktmc

import kotlinx.serialization.Serializable
import kotlin.random.Random

abstract class Config

@Serializable
data class ClientConfig(
    var address: String = "127.0.0.1",
    var port: Int = 25565,
    var username: String = randomAlphanumeric(16),
    var password: String = "",
    var logConnection: Boolean = true,
    var logRespawns: Boolean = true,
    var logChat: Boolean = true
) : Config()

@Serializable
data class ChatBotConfig(
    var config: ClientConfig = ClientConfig(),
    var prefix: String = "!"
) : Config()

@Serializable
class DiscordConfig(val token: String = "", val prefix: String = "!") : Config()

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"