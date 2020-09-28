package cli

import cli.commands.LaunchInstance
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import minecraft.MinecraftClient
import minecraft.bot.Cmd
import minecraft.bot.CommandManager
import minecraft.bot.Message

class CLI {
    val instances = HashMap<String, MinecraftClient>()

    private val dispatcher = CommandDispatcher<Cmd<CLIMessage>>()
    private val commandManager = CommandManager<CLIMessage>()

    init {
        commandManager.loadCommands(hashSetOf(LaunchInstance()))
        commandManager.registerCommands(dispatcher)

        while (true) {
            print("hl-mc-kt > ")
            val commandString = readLine()
            if (commandString != null) {
                if (commandString.isNotEmpty()) {
                    val event = CLIMessage(commandString, this)
                    val command = Cmd<CLIMessage>()
                    try {
                        dispatcher.execute(commandString, command)
                        command.execute(event)
                    } catch (_: CommandSyntaxException) {
                        println("Command failed to execute: $command")
                        if (commandManager.commandExists(commandString.split(" ")[0])) {
                            println("Invalid syntax!")
                        }
                    }
                }
            }
        }
    }
}

class CLIMessage(message: String, val cli: CLI) : Message(message)