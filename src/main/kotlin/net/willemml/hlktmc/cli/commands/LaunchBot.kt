package net.willemml.hlktmc.cli.commands

import chatBotManager
import net.willemml.hlktmc.cli.CLIMessage
import net.willemml.ktcmd.Command
import net.willemml.hlktmc.ChatBotConfig
import net.willemml.hlktmc.ClientConfig
import net.willemml.hlktmc.randomAlphanumeric

val launchAll = Command<CLIMessage>("launchall", "Launches all Minecraft bots.", arrayListOf("la")) {
    chatBotManager.startAll(getArgument("delay"))
}.apply {
    long("delay", false, "How long to wait between connecting each bot.", "d", 0)
}

val launchMinecraftBot = Command<CLIMessage>("launch", "Launches an instance of Minecraft chat bot.", arrayListOf("l")) {
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