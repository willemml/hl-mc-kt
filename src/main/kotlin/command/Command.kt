package minecraft.bot

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import command.getUsageList
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class Command<T : Message>(name: String) : LiteralArgumentBuilder<Cmd<T>>(name)

abstract class Message(val message: String) {
    abstract fun info(message: String)
    abstract fun error(message: String)
    abstract fun success(message: String)
}

class Cmd<T : Message> {
    private val actions = ArrayList<T.() -> Unit>()

    infix fun runs(action: T.() -> Unit) {
        actions.add(action)
    }

    fun execute(message: T) {
        for (action in actions) GlobalScope.launch { action.invoke(message) }
    }
}

class CommandManager<T : Message>(private val dispatcher: CommandDispatcher<Cmd<T>>) {
    private val commands = HashMap<String, Command<T>>()

    fun loadCommands(newCommands: HashSet<Command<T>>) {
        for (command in newCommands) {
            commands[command.literal] = command
            dispatcher.register(commands[command.literal])
        }
    }

    fun executeCommand(cause: T) {
        if (cause.message.isNotEmpty()) {
            val command = Cmd<T>()
            try {
                dispatcher.execute(cause.message, command)
                println(dispatcher.getAllUsage(dispatcher.root, command, false))
                command.execute(cause)
            } catch (_: CommandSyntaxException) {
                val commandName = cause.message.split(" ")[0]
                if (commands.containsKey(commandName)) {
                    cause.error("Invalid syntax.")
                    commands[commandName]?.getUsageList()?.let { cause.info(it) }
                } else {
                    cause.error("No command with name \"$commandName\".")
                }
            }
        }
    }
}