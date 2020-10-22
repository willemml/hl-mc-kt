package net.willemml.hlktmc.minecraft.bot.commands

import net.willemml.hlktmc.minecraft.bot.ChatMessage
import net.willemml.ktcmd.Command

val echo = Command<ChatMessage>("echo", "Repeat a piece of text.", arrayListOf("e"), true) {
    it.respond(getArgument("text"))
}.apply {
    string("text", true, "Text to repeat")
}