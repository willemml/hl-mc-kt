import dev.wnuke.hlktmc.cli.CLI
import dev.wnuke.hlktmc.discord.DiscordBotManager
import dev.wnuke.hlktmc.minecraft.bot.ChatBotManager
import kotlinx.serialization.InternalSerializationApi

val chatBotManager = ChatBotManager("config/minecraft-bots.json")
val discordBotManager = DiscordBotManager("config/discord-bots.json")

@ExperimentalUnsignedTypes
@InternalSerializationApi
fun main() {
    server()
    CLI()
}