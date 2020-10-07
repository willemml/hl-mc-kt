package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.startDiscordBot
import dev.wnuke.hlktmc.startMinecraftBot
import dev.wnuke.ktcmd.Command

val connectMinecraftBot = Command<CLIMessage>("connectmcb", "Connects a Minecraft bot.", arrayListOf("cm", "cmc")) {
    if (startMinecraftBot(getArgument("botname"))) it.success("Bot started.") else it.error("No bot with that name.")
}.apply {
    string("botname", true, "Name of the bot to connect", "b")
}

val connectDiscordBot = Command<CLIMessage>("connectdb", "Connects a Discord bot.", arrayListOf("cd", "cdb")) {
    if (startDiscordBot(getArgument("botname"))) it.success("Bot started.") else it.error("No bot with that name.")
}.apply {
    string("botname", true, "Name of the bot to connect", "b")
}