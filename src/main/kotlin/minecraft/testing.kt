package minecraft

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import minecraft.bot.ChatBot

fun clientTest() {
    GlobalScope.launch {
        val client = ChatBot()
        client.connect()
        client.sendMessage("Hello command.from hl-mc-kt!")
    }
}