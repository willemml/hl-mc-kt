package dev.wnuke.hlktmc.minecraft.bot

import com.google.gson.JsonElement
import dev.wnuke.hlktmc.minecraft.BasicClient
import dev.wnuke.hlktmc.minecraft.ClientConfig
import dev.wnuke.hlktmc.minecraft.bot.commands.echo
import dev.wnuke.ktcmd.Call
import dev.wnuke.ktcmd.CommandManager
import java.util.*

class ChatBot(clientConfig: ClientConfig = ClientConfig(), private val commandPrefix: String = "!") : BasicClient(clientConfig) {
    private val commandManager = CommandManager<ChatMessage>().apply { addCommand(echo) }

    override fun onChat(message: String, sender: UUID, rawMessage: JsonElement) {
        if (message.startsWith(commandPrefix) || commandPrefix.isEmpty()) {
            val cause = ChatMessage(message.removePrefix(commandPrefix), sender, this, rawMessage)
            commandManager.runCommand(cause)
        }
    }
}

open class ChatMessage(message: String, val sender: UUID, val client: BasicClient, val rawMessage: JsonElement) : Call(message) {
    override fun info(message: String) {
        respond("Info: $message")
    }

    override fun respond(message: String) {
        client.sendMessage(message)
    }

    override fun error(message: String) {
        respond("Error: $message")
    }

    override fun success(message: String) {
        respond("Success: $message")
    }
}

class WhisperMessage(message: String, sender: UUID, client: BasicClient, rawMessage: JsonElement) : ChatMessage(message, sender, client, rawMessage) {
    override fun respond(message: String) {
        client.sendMessage("/msg $sender $message")
    }
}