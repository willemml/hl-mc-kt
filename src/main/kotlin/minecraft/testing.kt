package minecraft

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.CommandSyntaxException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import minecraft.bot.Cmd
import minecraft.bot.CommandManager
import java.util.*

fun clientTest() {
    val prefix = "!"
    val dispatcher = CommandDispatcher<Cmd>()
    val commandManager = CommandManager()
    commandManager.loadCommands()
    commandManager.registerCommands(dispatcher)
    println(commandManager.commands.keys.joinToString(", "))
    GlobalScope.launch {
        val client = object : MinecraftClient("127.0.0.1", 25565, MinecraftProtocol("clientTest")) {
            override fun onChat(message: String, sender: UUID) {
                if (message.startsWith(prefix)) {
                    val commandString = message.removePrefix(prefix)
                    val event = MessageEvent(this, message, sender)
                    val command = Cmd()
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
        client.connect()
        client.sendMessage("Hello from hl-mc-kt!")
    }
}