package dev.wnuke.hlktmc

import discordBotConfigs
import discordConfigFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import minecraftBotConfigs
import minecraftConfigFile

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

fun writeMinecraftConfig() {
    minecraftConfigFile.parentFile.mkdirs()
    minecraftConfigFile.createNewFile()
    minecraftConfigFile.writeText(Json.encodeToString(minecraftBotConfigs))
}

fun writeDiscordConfig() {
    discordConfigFile.parentFile.mkdirs()
    discordConfigFile.createNewFile()
    discordConfigFile.writeText(Json.encodeToString(discordBotConfigs))
}

fun readDiscordConfig() {
    discordBotConfigs = Json.decodeFromString(discordConfigFile.readText())
}

fun readMinecraftConfig() {
    minecraftBotConfigs = Json.decodeFromString(minecraftConfigFile.readText())
}
