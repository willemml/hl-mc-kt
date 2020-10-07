package dev.wnuke.hlktmc

import com.github.steveice10.mc.protocol.MinecraftProtocol
import dev.wnuke.hlktmc.discord.Discord
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.minecraft.bot.ChatBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

var discordBotConfigs = HashMap<String, DiscordConfig>()
var minecraftBotConfigs = HashMap<String, ChatBotConfig>()
val discordBots = HashMap<String, Discord>()
val minecraftBots = HashMap<String, ChatBot>()
val minecraftConfigFile =
    File("config/minecraft-bots.json").apply {
        parentFile.mkdirs()
        if (createNewFile()) this.writeText(Json.encodeToString(minecraftBotConfigs))
    }
val discordConfigFile =
    File("config/discord-bots.json").apply {
        parentFile.mkdirs()
        if (createNewFile()) this.writeText(Json.encodeToString(discordBotConfigs))
    }

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
    try {
        discordBotConfigs = Json.decodeFromString(discordConfigFile.readText())
    } catch (e: SerializationException) {
        println("Broken Discord config file, backing up and resetting...")
        discordConfigFile.copyTo(File("config/discord-bots-old.json"), true)
        discordConfigFile.delete()
        writeDiscordConfig()
    }
}

fun readMinecraftConfig() {
    try {
        minecraftBotConfigs = Json.decodeFromString(minecraftConfigFile.readText())
    } catch (e: SerializationException) {
        println("Broken Minecraft config file, backing up and resetting...")
        minecraftConfigFile.copyTo(File("config/minecraft-bots-old.json"), true)
        minecraftConfigFile.delete()
        writeMinecraftConfig()
    }
}

fun addMinecraftBot(name: String, server: String, port: Int, username: String, password: String, prefix: String, connectionLogs: Boolean, chatLogs: Boolean) {
    readMinecraftConfig()
    minecraftBotConfigs[name] = ChatBotConfig(username, password, server, port, prefix, connectionLogs, chatLogs)
    writeMinecraftConfig()
}

fun addDiscordBot(name: String, token: String, prefix: String) {
    readDiscordConfig()
    discordBotConfigs[name] = DiscordConfig(token, prefix)
    writeDiscordConfig()
}

fun removeMinecraftBot(name: String) {
    readMinecraftConfig()
    minecraftBotConfigs.remove(name)
    minecraftBots[name]?.disconnect()
    minecraftBots.remove(name)
    writeMinecraftConfig()
}

fun removeDiscordBot(name: String) {
    readDiscordConfig()
    discordBotConfigs.remove(name)
    discordBots[name]?.stop()
    discordBots.remove(name)
    writeDiscordConfig()
}

fun startMinecraftBot(name: String): Boolean {
    readMinecraftConfig()
    val bot = minecraftBotConfigs[name]?: return false
    GlobalScope.launch {
        minecraftBots[name] = ChatBot(
            ClientConfig(
                bot.server,
                bot.port,
                if (bot.password.isNotEmpty()) MinecraftProtocol(
                    bot.username,
                    bot.password
                ) else MinecraftProtocol(bot.username),
                connectionLogs = bot.connectionLogs,
                chatLogs = bot.chatLogs
            ), bot.prefix
        ).apply {
            connect()
        }
    }
    return true
}

fun startDiscordBot(name: String): Boolean {
    val bot = discordBotConfigs[name]?: return false
    GlobalScope.launch {
        discordBots[name] = Discord(bot.token, bot.prefix).apply { start() }
    }
    return true
}

fun startDiscordBots() {
    readDiscordConfig()
    println("Starting Discord bots...")
    val bots = ArrayList<String>()
    for (name in discordBotConfigs.keys) {
        if (startDiscordBot(name)) bots.add(name)
    }
    if (bots.isNotEmpty()) println("Started Discord Bots: ${bots.joinToString()}")
}

fun startMinecraftBots() {
    println("Starting Minecraft bots...")
    val bots = ArrayList<String>()
    GlobalScope.launch {
        for (name in minecraftBotConfigs.keys) {
            if (startMinecraftBot(name)) {
                bots.add(name)
                if (minecraftBotConfigs[name]?.password?.isNotEmpty() == true) delay(6000)
            }
        }
    }
    if (bots.isNotEmpty()) println("Started Minecraft Bots: ${bots.joinToString()}")
}