package cli.commands

import cli.CLIMessage
import com.github.steveice10.mc.protocol.MinecraftProtocol
import command.from
import command.integer
import command.runs
import command.string
import minecraft.ClientConfig
import minecraft.MinecraftClient
import minecraft.bot.Command

class LaunchInstance : Command<CLIMessage>("launch") {
    init {
        string("name") {
            runs {context ->
                cli.instances["name" from context] = MinecraftClient()
                println("Created.")
            }
            string("host") {
                runs {context ->
                    cli.instances["name" from context] = MinecraftClient(ClientConfig("host" from context))
                    println("Created.")
                }
                integer("port") {
                    runs {context ->
                        cli.instances["name" from context] = MinecraftClient(ClientConfig("host" from context, "port" from context))
                        println("Created.")
                    }
                    string("username") {
                        runs {context ->
                            val username: String = "username" from context
                            cli.instances["name" from context] = MinecraftClient(ClientConfig("host" from context, "port" from context, MinecraftProtocol(username)))
                            println("Created.")
                        }
                        string("password") {
                            runs {context ->
                                val username: String = "username" from context
                                val password: String = "password" from context
                                cli.instances["name" from context] = MinecraftClient(ClientConfig("host" from context, "port" from context, MinecraftProtocol(username, password)))
                                println("Created.")
                            }
                        }
                    }
                }
            }
        }
    }
}