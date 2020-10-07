package dev.wnuke.hlktmc

import com.github.steveice10.mc.protocol.MinecraftProtocol
import dev.wnuke.hlktmc.discord.Discord
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.minecraft.bot.ChatBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        )
    }
    return true
}

fun startDiscordBot(name: String): Boolean {
    val bot = discordBotConfigs[name]?: return false
    GlobalScope.launch {
        discordBots[name] = Discord(bot.token, bot.prefix)
    }
    return true
}

fun startDiscordBots() {
    println("Starting Discord bots...")
    val bots = ArrayList<String>()
    for (name in discordBotConfigs.keys) {
        if (startDiscordBot(name)) bots.add(name)
    }
    println("Started Discord Bots: ${bots.joinToString()}")
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
    println("Started Minecraft Bots: ${bots.joinToString()}")
}