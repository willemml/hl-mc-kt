package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command

val removeBot = Command<CLIMessage>("remove", "Removes a bot.", arrayListOf("rm"), true) {
    if (!chatBotManager.remove(getArgument("botname"))) it.error("No bot with that name.")
}.apply {
    string("botname", true, "Name of the bot to remove")
}