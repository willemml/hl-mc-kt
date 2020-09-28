package minecraft.bot

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.exceptions.CommandSyntaxException
import command.from
import command.getUsageList
import command.runs
import command.string
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class Command<T : Message>(name: String, val description: String = "") : LiteralArgumentBuilder<Cmd<T>>(name)

class Help<T : Message>(commandManager: CommandManager<T>) : Command<T>("help", "List available commands and get their usages.") {
    init {
        string("name") {
            runs { context ->
                val name: String = "name" from context
                if (commandManager.commandExists(name)) {
                    commandManager.commandHelp(name)?.let { info(it) }
                } else {
                    error("No command with name \"$name\".")
                }
            }
        }
        runs {
            info(commandManager.listCommands())
        }
    }
}

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

    init {
        loadCommands(hashSetOf(Help(this)))
    }

    fun loadCommands(newCommands: HashSet<Command<T>>) {
        for (command in newCommands) {
            if (!commands.containsKey(command.literal)) {
                commands[command.literal] = command
                dispatcher.register(commands[command.literal])
            }
        }
    }

    fun listCommands() =
        "Available Commands: ${commands.entries.joinToString(separator = "") { "\n - ${it.key}${if (it.value.description.isNotEmpty()) ": ${it.value.description}" else ""}" }}"

    fun commandExists(name: String) = commands.containsKey(name)

    fun commandHelp(name: String) = commands[name]?.getUsageList()

    fun executeCommand(cause: T) {
        if (cause.message.isNotEmpty()) {
            val command = Cmd<T>()
            try {
                dispatcher.execute(cause.message, command)
                println(dispatcher.getAllUsage(dispatcher.root, command, false))
                command.execute(cause)
            } catch (_: CommandSyntaxException) {
                val commandName = cause.message.split(" ")[0]
                if (commandExists(commandName)) {
                    cause.error("Invalid syntax.")
                    commandHelp(commandName)?.let { cause.info(it) }
                } else {
                    cause.error("No command with name \"$commandName\".")
                }
            }
        }
    }
}