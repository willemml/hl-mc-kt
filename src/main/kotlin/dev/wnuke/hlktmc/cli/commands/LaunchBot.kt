package dev.wnuke.hlktmc.cli.commands

import chatBotManager
import dev.wnuke.hlktmc.*
import dev.wnuke.hlktmc.cli.CLIMessage
import dev.wnuke.ktcmd.Command
import discordBotManager

val launchMinecraftBot = Command<CLIMessage>("launchcb", "Launches an instance of Minecraft chat bot.", arrayListOf("lcb", "lc", "lmc")) {
    val name = getArgument<String>("name")
    val username = getOptionalArgument<String>("username")
    val config = ChatBotConfig(ClientConfig(
            getOptionalArgument("host"),
            getOptionalArgument("port"),
            if (username.isEmpty()) randomAlphanumeric(10) else username,
            if (username.isEmpty()) "" else getOptionalArgument("password"),
            getOptionalArgument("log_connection"),
            getOptionalArgument("log_respawns"),
            getOptionalArgument("log_chat")
    ), getOptionalArgument("prefix"))
    chatBotManager.add(name, config, getOptionalArgument("overwrite"))
    if (getOptionalArgument("start")) chatBotManager.start(name)
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("host", false, "IP/Address of the Minecraft server to connect to, localhost is default", "h", "localhost")
    integer("port", false, "Port of the Minecraft server to connect to, 25565 is default", "p", 25565)
    string("username", false, "Minecraft username/email, if blank a random string is used", "u")
    string("password", false, "Minecraft account password to use, goes to offline mode if none given", "pw")
    string("prefix", false, "Prefix to listen for commands with", "pf", "!")
    boolean("log_connection", false, "Whether or not to log connection status to console", "l", false)
    boolean("log_respawns", false, "Whether or not to log respawns to console", "cl", false)
    boolean("log_chat", false, "Whether or not to log chat messages to console", "cl", false)
    boolean("overwrite", false, "Whether or not to overwrite existing bots of the same name", "o", true)
    boolean("start", false, "Whether or not to start the bot immediately", "s", true)
}

val launchDiscordBot = Command<CLIMessage>("launchdb", "Launches an instance of the Discord bot.", arrayListOf("ldb", "ld")) {
    val name = getArgument<String>("name")
    val config = DiscordConfig(
            getArgument("token"),
            getOptionalArgument("prefix")
    )
    discordBotManager.add(name, config, getOptionalArgument("overwrite"))
    if (getOptionalArgument("start")) discordBotManager.start(name)
}.apply {
    string("name", true, "Name of the Discord bot instance", "n")
    string("token", true, "Discord bot token to use", "t")
    string("prefix", false, "Prefix to listen for commands with", "p", "!")
    boolean("start", false, "Whether or not to start the bot immediately", "s", true)
}

val launchAllBots = Command<CLIMessage>("launchbots", "Launches all bots from config file.", arrayListOf("lb")) {
    if (getOptionalArgument("minecraft")) chatBotManager.startAll(10000)
    if (getOptionalArgument("discord")) discordBotManager.startAll()
}.apply {
    boolean("discord", false, "Whether or not to start all Discord bots, defaults to true", "d", true)
    boolean("overwrite", false, "Whether or not to overwrite existing bots of the same name", "o", true)
    boolean("minecraft", false, "Whether or not to start all Minecraft bots, defaults to true", "m", true)
}