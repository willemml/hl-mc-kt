package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command

val sendFromMinecraftBot = Command<CLIMessage>("send", "Sends a message from a Minecraft bot instance.", arrayListOf("s"), true) {
    val bot = chatBotManager.bots[getArgument("botname")]
    if (bot == null) {
        it.error("No connected Minecraft instance with that name.")
        return@Command
    }
    bot.sendMessage(getArgument("message"))
}.apply {
    string("botname", true, "Name of the bot to remove")
    string("message", true, "Message to send")
}