package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.startMinecraftBot
import dev.wnuke.ktcmd.Command

val connectBot = Command<CLIMessage>("connect", "Connects a bot.", arrayListOf("c", "cb"), true) {
    if (startMinecraftBot(getArgument("botname")) || startMinecraftBot(getArgument("botname"))) it.success("Bot started.") else it.error(
        "No bot with that name."
    )
}.apply {
    string("botname", true, "Name of the bot to connect")
}