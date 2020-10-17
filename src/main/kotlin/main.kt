import dev.wnuke.hlktmc.cli.CLI
import dev.wnuke.hlktmc.discord.DiscordBotManager
import dev.wnuke.hlktmc.minecraft.bot.ChatBotManager
import kotlinx.serialization.InternalSerializationApi
import java.io.IOException

val chatBotManager = ChatBotManager("config/minecraft-bots.json")
val discordBotManager = DiscordBotManager("config/discord-bots.json")

@ExperimentalUnsignedTypes
@InternalSerializationApi
fun main() {
    var failed = true
    var i = 8080
    while (failed) {
        try {
            server(i)
            failed = false
        } catch (_: IOException) {
            i++
        }
    }
    CLI()
}