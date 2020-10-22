package net.willemml.hlktmc.cli

import net.willemml.hlktmc.cli.commands.*
import net.willemml.ktcmd.Call
import net.willemml.ktcmd.CommandManager
import net.willemml.hlktmc.cli.commands.*

class CLI {
    private val commandManager = CommandManager<CLIMessage>().apply {
        loadCommands(
            listOf(
                connectBot,
                launchAllBots,
                launchMinecraftBot,
                launchDiscordBot,
                listBots,
                removeBot,
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