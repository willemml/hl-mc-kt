package dev.wnuke.hlktmc.cli

import dev.wnuke.hlktmc.cli.commands.launchChatBot
import dev.wnuke.hlktmc.cli.commands.launchDiscordBot
import dev.wnuke.hlktmc.discord.Discord
import dev.wnuke.hlktmc.minecraft.MinecraftClient
import dev.wnuke.ktcmd.Call
import dev.wnuke.ktcmd.CommandManager

class CLI {
    val mcClients = HashMap<String, MinecraftClient>()
    val discordBots = HashMap<String, Discord>()

    private val commandManager = CommandManager<CLIMessage>().apply { loadCommands(listOf(launchChatBot, launchDiscordBot).toTypedArray()) }

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