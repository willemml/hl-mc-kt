package cli.commands

import cli.CLIMessage
import com.github.steveice10.mc.protocol.MinecraftProtocol
import command.from
import command.integer
import command.runs
import command.string
import kotlinx.coroutines.runBlocking
import minecraft.ClientConfig
import minecraft.bot.ChatBot
import minecraft.bot.Command

class LaunchChatBot : Command<CLIMessage>("launchcb", "Launch instances of the Chat Bot") {
    init {
        string("name") {
            string("host") {
                integer("port") {
                    string("username") {
                        string("password") {
                            runs {context ->
                                val username: String = "username" from context
                                val password: String = "password" from context
                                cli.mcClients["name" from context] = ChatBot(ClientConfig("host" from context, "port" from context, MinecraftProtocol(username, password))).apply { runBlocking { connect() } }
                                success("instance created")
                            }
                        }
                    }
                }
            }
        }
    }
}