package dev.wnuke.hlktmc.discord.commands

import dev.wnuke.hlktmc.discord.DiscordMessage
import dev.wnuke.ktcmd.Command

val ping = Command<DiscordMessage>("ping", "Says \"Pong!\".") {
    it.success("Pong!")
}