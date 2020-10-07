package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.addDiscordBot
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.ktcmd.Command

val launchDiscordBot = Command<CLIMessage>("launchdb", "Launches an instance of the Discord bot.") {
    addDiscordBot(getArgument("name"), getArgument("token"), getOptionalArgument("prefix"))
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("token", true, "Discord bot token to use", "t")
    string("prefix", false, "Prefix to look for when running commands", "p", "!")
}