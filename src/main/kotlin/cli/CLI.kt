package cli

import cli.commands.LaunchInstance
import com.mojang.brigadier.CommandDispatcher
import minecraft.MinecraftClient
import minecraft.bot.Cmd
import minecraft.bot.CommandManager
import minecraft.bot.Message

class CLI {
    val instances = HashMap<String, MinecraftClient>()

    private val commandManager = CommandManager(CommandDispatcher<Cmd<CLIMessage>>()).apply { loadCommands(hashSetOf(LaunchInstance())) }

    init {
        while (true) {
            print("hl-mc-kt > ")
            val commandString = readLine()
            commandString?.let { commandManager.executeCommand(CLIMessage(commandString, this)) }
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