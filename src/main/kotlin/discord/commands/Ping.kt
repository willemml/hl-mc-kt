package discord.commands

import command.runs
import discord.DiscordMessage
import minecraft.bot.Command

class Ping : Command<DiscordMessage>("ping", "Makes the bot say \"pong\"") {
    init {
        runs {
            success("Pong!")
        }
    }
}