package minecraft.bot

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class Command<T : Message>(name: String) : LiteralArgumentBuilder<Cmd<T>>(name)

open class Message(val message: String)

class Cmd<T : Message> {
    private val actions = ArrayList<T.() -> Unit>()

    infix fun runs(action: T.() -> Unit) {
        actions.add(action)
    }

    fun execute(message: T) {
        for (action in actions) GlobalScope.launch { action.invoke(message) }
    }
}

class CommandManager<T : Message> {
    val commands = HashMap<String, Command<T>>()

    fun loadCommands(newCommands: HashSet<Command<T>>) {
        for (command in newCommands) {
            loadCommand(command)
        }
    }

    fun loadCommand(command: Command<T>) {
        commands[command.literal] = command
    }

    fun commandExists(name: String) = commands.containsKey(name)


    fun getCommand(name: String) = commands[name]

    fun registerCommands(dispatcher: CommandDispatcher<Cmd<T>>) {
        for (command in commands) {
            dispatcher.register(command.value)
        }
    }
}