package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command
import discordBotManager

val connectBot = Command<CLIMessage>("connect", "Connects a bot.", arrayListOf("c", "cb"), true) {
    if (chatBotManager.start(getArgument("botname")) || discordBotManager.start(getArgument("botname"))) it.success("Bot started.") else it.error(
        "No bot with that name."
    )
}.apply {
    string("botname", true, "Name of the bot to connect")
}