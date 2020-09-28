package discord

import com.mojang.brigadier.CommandDispatcher
import discord.commands.Ping
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import minecraft.bot.CommandManager
import minecraft.bot.Message
import net.ayataka.kordis.Kordis
import net.ayataka.kordis.event.EventHandler
import net.ayataka.kordis.event.events.message.MessageReceiveEvent
import java.awt.Color

class Discord(private val botToken: String, private val commandPrefix: String = "!") {
    private val commandManager = CommandManager<DiscordMessage>(CommandDispatcher()).apply { loadCommands(hashSetOf(Ping())) }

    suspend fun start() {
        val client = Kordis.create {
            token = botToken
            addListener(this@Discord)
        }
    }

    @EventHandler
    fun onMessageReceive(event: MessageReceiveEvent) {
        if (event.message.content.startsWith(commandPrefix)) {
            commandManager.executeCommand(DiscordMessage(event, event.message.content.removePrefix(commandPrefix)))
        }
    }
}

class DiscordMessage(private val event: MessageReceiveEvent, message: String) : Message(message) {
    override suspend fun plain(message: String) {
        event.message.channel.send(message)
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