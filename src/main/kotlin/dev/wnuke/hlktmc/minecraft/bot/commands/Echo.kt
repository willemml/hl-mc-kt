package dev.wnuke.hlktmc.minecraft.bot.commands

import dev.wnuke.hlktmc.minecraft.bot.ChatMessage
import dev.wnuke.ktcmd.Command

val echo = Command<ChatMessage>("echo", "Repeat a piece of text") {
    (getArgument("text") as String?)?. let { text -> it.respond(text) }
}.apply {
    string("text", true, "Text to repeat", "t")
}