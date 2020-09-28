package minecraft.bot

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import minecraft.ClientConfig
import minecraft.MinecraftClient
import minecraft.bot.commands.Echo
import java.util.*

class ChatBot(clientConfig: ClientConfig = ClientConfig(), private val commandPrefix: String = "!") : MinecraftClient(clientConfig) {
    private val dispatcher = CommandDispatcher<Cmd<ChatMessage>>()
    private val commandManager = CommandManager<ChatMessage>()

    init {
        commandManager.loadCommands(hashSetOf(Echo()))
        commandManager.registerCommands(dispatcher)
    }

    override fun onChat(message: String, sender: UUID) {
        if (message.startsWith(commandPrefix) || commandPrefix.isEmpty()) {
            val commandString = message.removePrefix(commandPrefix)
            val event = ChatMessage(message, sender, this)
            val command = Cmd<ChatMessage>()
            try {
                dispatcher.execute(commandString, command)
                command.execute(event)
            } catch (_: CommandSyntaxException) {
                println("Command failed to execute: $command")
                if (commandManager.commandExists(commandString.split(" ")[0])) {
                    sendMessage("Invalid syntax!")
                }
            }
        }
    }
}

class ChatMessage(message: String, val sender: UUID, val client: MinecraftClient) : Message(message)