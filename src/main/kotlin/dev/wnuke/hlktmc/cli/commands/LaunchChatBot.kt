package dev.wnuke.hlktmc.cli.commands

import dev.wnuke.hlktmc.addMinecraftBot
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.hlktmc.randomAlphanumeric
import dev.wnuke.hlktmc.startMinecraftBot
import dev.wnuke.ktcmd.Command

val launchChatBot = Command<CLIMessage>("launchcb", "Launches an instance of Minecraft chat bot.") {
    val name = getArgument<String>("name")
    val username = getOptionalArgument<String>("username")
    addMinecraftBot(
        name,
        getOptionalArgument("host"),
        getOptionalArgument("port"),
        if (username.isEmpty()) randomAlphanumeric(10) else username,
        if (username.isEmpty()) "" else getOptionalArgument("password"),
        getOptionalArgument("prefix"),
        getOptionalArgument("connection_log"),
        getOptionalArgument("chat_log")
    )
    if (getOptionalArgument("start")) startMinecraftBot(name)
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("host", false, "IP/Address of the Minecraft server to connect to, localhost is default", "h", "localhost")
    integer("port", false, "Port of the Minecraft server to connect to, 25565 is default", "p", 25565)
    string("username", false, "Minecraft username/email, if blank a random string is used", "u")
    string("password", false, "Minecraft account password to use, goes to offline mode if none given", "pw")
    string("prefix", false, "Prefix to listen for commands with", "pf", "!")
    boolean("chat_log", false, "Whether or not to log chat messages to console", "cl", false)
    boolean("connection_log", false, "Whether or not to log connection status to console", "l", false)
    boolean("start", false, "Whether or not to start the bot immediately", "s", true)
}