package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command
import discordBotManager

val removeBot = Command<CLIMessage>("removebot", "Removes a bot.", arrayListOf("rm", "rmb"), true) {
    chatBotManager.remove(getArgument("botname"))
    discordBotManager.remove(getArgument("botname"))
}.apply {
    string("botname", true, "Name of the bot to remove")
}