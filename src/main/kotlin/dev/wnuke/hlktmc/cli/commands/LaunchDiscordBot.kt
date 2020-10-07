package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.addDiscordBot
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.startDiscordBot
import dev.wnuke.ktcmd.Command

val launchDiscordBot = Command<CLIMessage>("launchdb", "Launches an instance of the Discord bot.") {
    val name = getArgument<String>("name")
    addDiscordBot(
        name,
        getArgument("token"),
        getOptionalArgument("prefix")
    )
    if (getOptionalArgument("start")) startDiscordBot(name)
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("token", true, "Discord bot token to use", "t")
    string("prefix", false, "Prefix to listen for commands with", "p", "!")
    boolean("start", false, "Whether or not to start the bot immediately", "s", true)
}