package minecraft.bot

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import does
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import minecraft.MessageEvent
import minecraft.bot.commands.Echo

open class Command(name: String) : LiteralArgumentBuilder<Cmd>(name) {
}

class Cmd(event: MessageEvent) {
    private val actions = ArrayList<MessageEvent.() -> Unit>()

    infix fun runs(action: MessageEvent.() -> Unit) {
        actions.add(action)
    }

    fun execute(message: MessageEvent) {
        for (action in actions) GlobalScope.launch { action.invoke(message) }
    }
}

class CommandManager() {
    val commands = HashMap<String, Command>()

    fun loadCommands() {
        commands["echo"] = Echo()
    }

    fun commandExists(name: String) = commands.containsKey(name)


    fun getCommand(name: String) = commands[name]

    fun registerCommands(dispatcher: CommandDispatcher<Cmd>) {
        for (command in commands) {
            dispatcher.register(command.value)
        }
    }
}