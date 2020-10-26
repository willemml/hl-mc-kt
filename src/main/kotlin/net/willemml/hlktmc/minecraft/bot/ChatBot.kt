package net.willemml.hlktmc.minecraft.bot

import com.github.steveice10.mc.protocol.data.game.MessageType
import net.willemml.hlktmc.ChatBotConfig
import net.willemml.hlktmc.minecraft.BasicClient
import net.willemml.ktcmd.Call
import net.willemml.ktcmd.CommandManager
import net.daporkchop.lib.minecraft.text.component.MCTextRoot
import net.willemml.hlktmc.minecraft.bot.commands.*
import java.util.*

class ChatBot(private val botConfig: ChatBotConfig) : BasicClient(botConfig.config) {
    private val commandManager = CommandManager<ChatMessage>().apply { loadCommands(arrayOf(echo, moveX, moveY, moveZ, square)) }

    override fun onChat(message: String, messageType: MessageType, sender: UUID, rawMessage: MCTextRoot) {
        val usernameRegexStrings = arrayOf("<\\w+> ",  "\\w+ >> ", "[D] \\w+ >> ")
        val usernameRegexArray = usernameRegexStrings.map { Regex(it) }
        var messageFormatted = message
        for (regex in usernameRegexArray) {
            val newMessage = message.replaceFirst(regex, "")
            if (newMessage != message) {
                messageFormatted = newMessage
                break
            }
        }
        if (messageFormatted.startsWith(botConfig.prefix) || botConfig.prefix.isEmpty()) {
            val cause = ChatMessage(messageFormatted.removePrefix(botConfig.prefix), sender, this, rawMessage)
            commandManager.runCommand(cause)
        }
    }
}

open class ChatMessage(message: String, val sender: UUID, val client: BasicClient, val rawMessage: MCTextRoot) : Call(message) {
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

class WhisperMessage(message: String, sender: UUID, client: BasicClient, rawMessage: MCTextRoot) : ChatMessage(message, sender, client, rawMessage) {
    override fun respond(message: String) {
        client.sendMessage("/msg $sender $message")
    }
}