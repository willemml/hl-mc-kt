package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.randomAlphanumeric
import dev.wnuke.ktcmd.Command

val launchChatBot = Command<CLIMessage>("launchcb", "Launches an instance of Minecraft chat bot.") {
    val name = getArgument<String>("name")
    val config = ClientConfig()
    config.port = getOptionalArgument("port")
    config.address = getOptionalArgument("host")
    val username = getOptionalArgument<String>("username")
    val password = getOptionalArgument<String>("password")

}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("host", false, "Discord bot token to use", "h")
    integer("port", false, "Discord bot token to use", "p")
    string("username", false, "Discord bot token to use", "u", randomAlphanumeric(10))
    string("password", false, "Discord bot token to use", "pw")
}