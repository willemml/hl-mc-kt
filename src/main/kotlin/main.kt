import net.willemml.hlktmc.cli.CLI
import net.willemml.hlktmc.discord.DiscordBotManager
import net.willemml.hlktmc.minecraft.bot.ChatBotManager
import java.io.IOException

val chatBotManager = ChatBotManager("config/minecraft-bots.json")
val discordBotManager = DiscordBotManager("config/discord-bots.json")

@ExperimentalUnsignedTypes
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