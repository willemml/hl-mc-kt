package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command

val connectBot = Command<CLIMessage>("connect", "Connects a bot.", arrayListOf("c"), true) {
    if (!chatBotManager.start(getArgument("botname"))) it.error("No bot with that name.")
}.apply {
    string("botname", true, "Name of the bot to connect")
}