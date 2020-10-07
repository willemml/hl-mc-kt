package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.removeDiscordBot
import dev.wnuke.hlktmc.removeMinecraftBot
import dev.wnuke.ktcmd.Command

val removeMinecraftBot = Command<CLIMessage>("removemcb", "Removes a Minecraft bot.", arrayListOf("rmm", "rmmc")) {
    removeMinecraftBot(getArgument("botname"))
}.apply {
    string("botname", true, "Name of the bot to remove", "b")
}

val removeDiscordBot = Command<CLIMessage>("removedb", "Removes a Discord bot.", arrayListOf("rmd", "rmdb")) {
    removeDiscordBot(getArgument("botname"))
}.apply {
    string("botname", true, "Name of the bot to remove", "b")
}