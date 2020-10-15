package dev.wnuke.hlktmc.minecraft.bot

import com.github.steveice10.mc.protocol.data.game.MessageType
import dev.wnuke.hlktmc.ClientConfig
import dev.wnuke.hlktmc.minecraft.BasicClient
import dev.wnuke.hlktmc.minecraft.bot.commands.echo
import dev.wnuke.ktcmd.Call
import dev.wnuke.ktcmd.CommandManager
import net.daporkchop.lib.minecraft.text.component.MCTextRoot
import java.util.*

class ChatBot(clientConfig: ClientConfig = ClientConfig(), private val commandPrefix: String = "!") : BasicClient(clientConfig) {
    private val commandManager = CommandManager<ChatMessage>().apply { addCommand(echo) }

    override fun onChat(message: String, messageType: MessageType, sender: UUID, rawMessage: MCTextRoot) {
        val usernameRegexStrings = arrayOf("<\\w+> ",  "\\w+ >> ")
        val usernameRegexArray = usernameRegexStrings.map { Regex(it) }
        var messageFormatted = message
        for (regex in usernameRegexArray) {
            val newMessage = message.replaceFirst(regex, "")
            if (newMessage != message) {
                messageFormatted = newMessage
                break
            }
        }
        if (messageFormatted.startsWith(commandPrefix) || commandPrefix.isEmpty()) {
            val cause = ChatMessage(messageFormatted.removePrefix(commandPrefix), sender, this, rawMessage)
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