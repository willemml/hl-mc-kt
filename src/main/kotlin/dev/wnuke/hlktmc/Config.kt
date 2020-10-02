package dev.wnuke.hlktmc

import kotlinx.serialization.Serializable
import randomAlphanumeric

@Serializable
class ChatBotConfig(val username: String = randomAlphanumeric(10), val password: String = "", val server: String = "127.0.0.1", val port: Int = 25565, val prefix: String = "!")

@Serializable
class DiscordConfig(val token: String = "", val prefix: String = "!")
