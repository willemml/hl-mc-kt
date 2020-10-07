package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.minecraftBots
import dev.wnuke.ktcmd.Command

val sendFromMinecraftBot = Command<CLIMessage>("sendmc", "Sends a message from a Minecraft bot instance.", arrayListOf("smc", "smcb")) {
    val name = getArgument<String>("botname")
    val bot = minecraftBots[name]
    if (bot == null) {
        it.error("No connected Minecraft instance with name $name")
        return@Command
    }
    bot.sendMessage(getArgument("message"))
}.apply {
    string("botname", true, "Name of the bot to remove", "b")
    string("message", true, "Message to send", "m")
}