package minecraft.bot

import com.github.steveice10.mc.protocol.data.message.style.ChatColor
import com.mojang.brigadier.CommandDispatcher
import minecraft.ClientConfig
import minecraft.MinecraftClient
import minecraft.bot.commands.Echo
import java.util.*

class ChatBot(clientConfig: ClientConfig = ClientConfig(), private val commandPrefix: String = "!") : MinecraftClient(clientConfig) {
    private val commandManager = CommandManager(CommandDispatcher<Cmd<ChatMessage>>()).apply { loadCommands(hashSetOf(Echo())) }

    override fun onChat(message: String, sender: UUID) {
        if (message.startsWith(commandPrefix) || commandPrefix.isEmpty()) {
            val cause = ChatMessage(message.removePrefix(commandPrefix), sender, this)
            commandManager.executeCommand(cause)
        }
    }
}

class ChatMessage(message: String, val sender: UUID, val client: MinecraftClient) : Message(message) {
    override fun info(message: String) {
        client.sendMessage(message)
    }

    override fun error(message: String) {
        client.sendMessage("${ChatColor.RED}Error: $message")
    }

    override fun success(message: String) {
        client.sendMessage("${ChatColor.GREEN}Success: $message")
    }
}

class WhisperMessage(message: String, val sender: UUID, val client: MinecraftClient) : Message(message) {
    override fun info(message: String) {
        client.sendMessage("/msg $sender $message")
    }

    override fun error(message: String) {
        client.sendMessage("/msg $sender ${ChatColor.RED}Error: $message")
    }

    override fun success(message: String) {
        client.sendMessage("/msg $sender ${ChatColor.GREEN}Success: $message")
    }

}