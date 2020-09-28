package minecraft.bot.commands

import from
import greedyString
import minecraft.bot.Command
import minecraft.bot.runs

class Echo : Command("echo") {
    init {
        greedyString("message") {
            runs {context ->
                val message: String = "message" from context
                client.sendMessage(message)
            }
        }
    }
}