package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.discordBotConfigs
import dev.wnuke.hlktmc.minecraftBotConfigs
import dev.wnuke.hlktmc.readDiscordConfig
import dev.wnuke.hlktmc.readMinecraftConfig
import dev.wnuke.ktcmd.Command

val listBots = Command<CLIMessage>("listbots", "Lists all bots.") {
    readDiscordConfig()
    readMinecraftConfig()
    if (getOptionalArgument("minecraft")) println("Minecraft bots: ${minecraftBotConfigs.keys.joinToString()}")
    if (getOptionalArgument("discord")) println("Discord bots: ${discordBotConfigs.keys.joinToString()}")
}.apply {
    boolean("discord", false, "Whether or not to list Discord bots, defaults to true", "d", true)
    boolean("minecraft", false, "Whether or not to list Minecraft bots, defaults to true", "m", true)
}