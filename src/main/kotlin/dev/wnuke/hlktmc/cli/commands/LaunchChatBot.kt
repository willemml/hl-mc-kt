package dev.wnuke.hlktmc.cli.commands

import com.github.steveice10.mc.protocol.MinecraftProtocol
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.minecraft.bot.ChatBot
import dev.wnuke.ktcmd.Command

val launchChatBot = Command<CLIMessage>("launchcb", "Launches an instance of Minecraft chat bot") {
    (getArgument("name") as String?)?.let { name ->
        val config = ClientConfig()
        (getArgument("host") as String?)?.let { config.address = it }
        (getArgument("port") as Int?)?.let { config.port = it }
        val username = getArgument("username") as String?
        val password = getArgument("password") as String?
        username?.let {
            if (password == null) {
                MinecraftProtocol(it)
            } else {
                MinecraftProtocol(password)
            }
        }
        it.cli.mcClients[name] = ChatBot(config)
    }

}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("host", false, "Discord bot token to use", "h")
    integer("port", false, "Discord bot token to use", "p")
    string("username", false, "Discord bot token to use", "u")
    string("password", false, "Discord bot token to use", "pw")
}