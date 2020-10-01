package dev.wnuke.hlktmc.minecraft.bot.commands

import dev.wnuke.hlktmc.minecraft.bot.ChatMessage
import dev.wnuke.ktcmd.Command

val echo = Command<ChatMessage>("echo", "Repeat a piece of text.") {
    it.respond(getArgument("text"))
}.apply {
    string("text", true, "Text to repeat", "t")
}