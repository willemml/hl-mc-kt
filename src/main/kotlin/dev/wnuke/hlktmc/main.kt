
import com.github.steveice10.mc.protocol.MinecraftProtocol
import dev.wnuke.hlktmc.ChatBotConfig
import dev.wnuke.hlktmc.DiscordConfig
import dev.wnuke.hlktmc.cli.CLI
import dev.wnuke.hlktmc.discord.Discord
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.minecraft.bot.ChatBot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

var discordBots = ArrayList<DiscordConfig>()
var minecraftBots = ArrayList<ChatBotConfig>()
val minecraftConfigFile =
    File("config/minecraft-bots.json").apply {
        parentFile.mkdirs()
        if (createNewFile()) this.writeText(Json.encodeToString(minecraftBots)) }
val discordConfigFile =
    File("config/discord-bots.json").apply {
        parentFile.mkdirs()
        if (createNewFile()) this.writeText(Json.encodeToString(discordBots))
    }

@ExperimentalUnsignedTypes
@InternalSerializationApi
fun main() {
    readConfigs()
    startBots()
    CLI()
}

fun readConfigs() {
    discordBots = Json.decodeFromString(discordConfigFile.readText())
    minecraftBots = Json.decodeFromString(minecraftConfigFile.readText())
}

fun startBots() {
    for (bot in discordBots) {
        GlobalScope.launch { Discord(bot.token, bot.prefix).start() }
    }
    GlobalScope.launch {
        for (bot in minecraftBots) {
            GlobalScope.launch {
                ChatBot(
                    ClientConfig(
                        bot.server,
                        bot.port,
                        if (bot.password.isNotEmpty()) MinecraftProtocol(
                            bot.username,
                            bot.password
                        ) else MinecraftProtocol(bot.username),
                        connectionLogs = false,
                        chatLogs = false
                    ), bot.prefix
                ).connect()
            }
            delay(6000)
        }
    }
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}