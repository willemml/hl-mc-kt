package dev.wnuke.hlktmc.cli

import dev.wnuke.hlktmc.cli.commands.*
import dev.wnuke.ktcmd.Call
import dev.wnuke.ktcmd.CommandManager

class CLI {
    private val commandManager = CommandManager<CLIMessage>().apply {
        loadCommands(
            listOf(
                connectDiscordBot,
                connectMinecraftBot,
                launchAllBots,
                launchMinecraftBot,
                launchDiscordBot,
                listBots,
                removeDiscordBot,
                removeMinecraftBot,
                sendFromMinecraftBot
            ).toTypedArray()
        )
    }

    init {
        println(commandManager.listCommands())
        while (true) {
            print("hl-mc-kt > ")
            val commandString = readLine()
            commandString?.let { commandManager.runCommand(CLIMessage(commandString, this)) }
        }
    }
}

class CLIMessage(message: String, val cli: CLI) : Call(message) {
    override fun respond(message: String) {
        println(message)
    }

    override fun error(message: String) {
        respond("Error: $message")
    }

    override fun success(message: String) {
        respond("Success: $message")
    }

    override fun info(message: String) {
        respond("Info: $message")
    }
}