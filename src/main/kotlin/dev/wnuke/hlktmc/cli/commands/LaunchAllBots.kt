package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.startDiscordBots
import dev.wnuke.hlktmc.startMinecraftBots
import dev.wnuke.ktcmd.Command

val launchAllBots = Command<CLIMessage>("launchbots", "Launches all bots from config file.") {
    if (getOptionalArgument("minecraft")) startMinecraftBots()
    if (getOptionalArgument("discord")) startDiscordBots()
}.apply {
    boolean("discord", false, "Whether or not to start all Discord bots, defaults to true", "d", true)
    boolean("minecraft", false, "Whether or not to start all Minecraft bots, defaults to true", "m", true)
}