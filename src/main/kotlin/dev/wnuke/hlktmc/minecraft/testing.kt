package dev.wnuke.hlktmc.minecraft

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import dev.wnuke.hlktmc.minecraft.bot.ChatBot

fun clientTest() {
    GlobalScope.launch {
        val client = ChatBot()
        client.connect()
        client.sendMessage("Hello from hl-mc-kt!")
    }
}