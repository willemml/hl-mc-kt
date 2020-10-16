package dev.wnuke.hlktmc.cli.commands

import chatBotManager
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.ktcmd.Command
import discordBotManager

val listBots = Command<CLIMessage>("listbots", "Lists all bots.", arrayListOf("l", "ls")) {
    if (getOptionalArgument("minecraft") && chatBotManager.botConfigs.isNotEmpty()) println("Minecraft bots: ${chatBotManager.botConfigs.keys.joinToString()}")
    if (getOptionalArgument("discord") && discordBotManager.botConfigs.isNotEmpty()) println("Discord bots: ${discordBotManager.botConfigs.keys.joinToString()}")
}.apply {
    boolean("discord", false, "Whether or not to list Discord bots, defaults to true", "d", true)
    boolean("minecraft", false, "Whether or not to list Minecraft bots, defaults to true", "m", true)
}