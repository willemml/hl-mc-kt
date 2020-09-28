package cli.commands

import cli.CLIMessage
import command.from
import command.greedyString
import command.runs
import command.string
import discord.Discord
import kotlinx.coroutines.runBlocking
import minecraft.bot.Command

class LaunchDiscordBot : Command<CLIMessage>("launchdb", "Launches an instance of the Discord bot.") {
    init {
        string("name") {
            greedyString("token") {
                runs { context ->
                    val name: String = "name" from context
                    val token: String = "token" from context
                    cli.discordBots[name] = Discord(token).apply { runBlocking { start() } }
                }
            }
        }
    }
}