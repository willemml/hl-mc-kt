package net.willemml.hlktmc.discord.commands

import net.willemml.hlktmc.discord.DiscordMessage
import net.willemml.ktcmd.Command

val ping = Command<DiscordMessage>("ping", "Says \"Pong!\".") {
    it.success("Pong!")
}