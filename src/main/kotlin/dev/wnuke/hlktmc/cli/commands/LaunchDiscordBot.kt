package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.discord.Discord
import dev.wnuke.ktcmd.Command

val launchDiscordBot = Command<CLIMessage>("launchdb", "Launches an instance of the Discord bot") {
    val name = getArgument("name") as String?
    val token = getArgument("token") as String?
    it.cli.discordBots[name!!] = Discord(token!!)
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("token", true, "Discord bot token to use", "t")
}