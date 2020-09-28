package minecraft.bot.commands

import command.from
import command.greedyString
import command.runs
import minecraft.bot.ChatMessage
import minecraft.bot.Command

class Echo : Command<ChatMessage>("echo", "Repeat the argument.") {
    init {
        greedyString("message") {
            runs {context ->
                val message: String = "message" from context
                info(message)
            }
        }
    }
}