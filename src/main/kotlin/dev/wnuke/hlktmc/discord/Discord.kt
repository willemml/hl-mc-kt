package dev.wnuke.hlktmc.discord

import dev.wnuke.hlktmc.discord.commands.ping
import dev.wnuke.ktcmd.Call
import dev.wnuke.ktcmd.CommandManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.ayataka.kordis.Kordis
import net.ayataka.kordis.event.EventHandler
import net.ayataka.kordis.event.events.message.MessageReceiveEvent
import java.awt.Color

class Discord(private val botToken: String, private val commandPrefix: String = "!") {
    private val commandManager = CommandManager<DiscordMessage>().apply { loadCommands(listOf(ping).toTypedArray()) }

    suspend fun start() {
        val client = Kordis.create {
            token = botToken
            addListener(this@Discord)
        }
    }

    @EventHandler
    fun onMessageReceive(event: MessageReceiveEvent) {
        if (event.message.content.startsWith(commandPrefix)) {
            commandManager.runCommand(DiscordMessage(event, event.message.content.removePrefix(commandPrefix)))
        }
    }
}

class DiscordMessage(private val event: MessageReceiveEvent, message: String) : Call(message) {
    override fun respond(message: String) {
        GlobalScope.launch {
            event.message.channel.send(message)
        }
    }

    override fun info(message: String) {
        GlobalScope.launch {
            event.message.channel.send {
                embed {
                    description = message
                }
            }
        }
    }

    override fun error(message: String) {
        GlobalScope.launch {
            event.message.channel.send {
                embed {
                    description = "Error: $message"
                    color = Color.RED
                }
            }
        }
    }

    override fun success(message: String) {
        GlobalScope.launch {
            event.message.channel.send {
                embed {
                    description = message
                    color = Color.GREEN
                }
            }
        }
    }
}