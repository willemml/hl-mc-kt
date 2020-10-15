package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.removeDiscordBot
import dev.wnuke.hlktmc.removeMinecraftBot
import dev.wnuke.ktcmd.Command

val removeBot = Command<CLIMessage>("removebot", "Removes a bot.", arrayListOf("rm", "rmb"), true) {
    removeMinecraftBot(getArgument("botname"))
    removeDiscordBot(getArgument("botname"))
}.apply {
    string("botname", true, "Name of the bot to remove")
}