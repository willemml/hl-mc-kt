package cli

import cli.commands.LaunchChatBot
import cli.commands.LaunchDiscordBot
import com.mojang.brigadier.CommandDispatcher
import discord.Discord
import minecraft.MinecraftClient
import minecraft.bot.Cmd
import minecraft.bot.CommandManager
import minecraft.bot.Message

class CLI {
    val mcClients = HashMap<String, MinecraftClient>()
    val discordBots = HashMap<String, Discord>()

    private val commandManager =
        CommandManager(CommandDispatcher<Cmd<CLIMessage>>()).apply { loadCommands(hashSetOf(LaunchChatBot(), LaunchDiscordBot())) }

    init {
        while (true) {
            print("hl-mc-kt > ")
            val commandString = readLine()
            commandString?.let { commandManager.executeCommand(CLIMessage(commandString, this@CLI)) }
        }
    }
}

class CLIMessage(message: String, val cli: CLI) : Message(message) {
    override fun info(message: String) {
        println(message)
    }

    override fun error(message: String) {
        println("Error: $message")
    }

    override fun success(message: String) {
        println("Success: $message")
    }
}