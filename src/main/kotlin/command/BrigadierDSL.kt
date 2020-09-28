package command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.CommandNode
import minecraft.bot.Cmd
import minecraft.bot.Message

@DslMarker
@Target(AnnotationTarget.TYPE)
annotation class BrigadierDsl

infix fun <S> CommandDispatcher<S>.register(builder: LiteralArgumentBuilder<S>) = this.register(builder)

/**
 * Creates a new [LiteralArgumentBuilder] without a parent.
 */
fun <T> rootLiteral(name: String, block: (@BrigadierDsl LiteralArgumentBuilder<T>).() -> Unit) =
    LiteralArgumentBuilder.literal<T>(name).also(block)

/**
 * Appends a new [command.literal](LiteralArgumentBuilder) to `this` [ArgumentBuilder].
 *
 * @param name the name of the command.literal argument
 * @param block the receiver function for further construction of the command.literal argument
 */
fun <T> ArgumentBuilder<T, *>.literal(name: String, block: (@BrigadierDsl LiteralArgumentBuilder<T>).() -> Unit) =
    then(rootLiteral(name, block))

/**
 * Appends a new required argument to `this` [ArgumentBuilder].
 *
 * @param name the name of the required argument
 * @param argument the type of required argument, for example [IntegerArgumentType]
 * @param block the receiver function for further construction of the required argument
 */
fun <S, T : ArgumentBuilder<S, T>, R> ArgumentBuilder<S, T>.argument(
    name: String,
    argument: ArgumentType<R>,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, R>).() -> Unit
) =
    then(RequiredArgumentBuilder.argument<S, R>(name, argument).also(block))

/**
 * A shorthand for appending a boolean required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.bool(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, Boolean>).() -> Unit
) =
    argument(name, BoolArgumentType.bool(), block)

/**
 * A shorthand for appending a command.double required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.double(
    name: String,
    block: RequiredArgumentBuilder<S, Double>.() -> Unit
) =
    argument(name, DoubleArgumentType.doubleArg(), block)

/**
 * A shorthand for appending a command.float required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.float(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, Float>).() -> Unit
) =
    argument(name, FloatArgumentType.floatArg(), block)

/**
 * A shorthand for appending a command.integer required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.integer(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, Int>).() -> Unit
) =
    argument(name, IntegerArgumentType.integer(), block)

/**
 * A shorthand for appending a `command.long` required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.long(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, Long>).() -> Unit
) =
    argument(name, LongArgumentType.longArg(), block)

/**
 * A shorthand for appending a command.string required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.string(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, String>).() -> Unit
) =
    argument(name, StringArgumentType.string(), block)

/**
 * A shorthand for appending a greedy command.string required argument to `this` [ArgumentBuilder]
 *
 * @see argument
 */
fun <S, T : ArgumentBuilder<S, T>> ArgumentBuilder<S, T>.greedyString(
    name: String,
    block: (@BrigadierDsl RequiredArgumentBuilder<S, String>).() -> Unit
) =
    argument(name, StringArgumentType.greedyString(), block)

/**
 * Sets the executes callback for `this` [ArgumentBuilder]
 *
 * @param command the callback
 */
infix fun <S> ArgumentBuilder<S, *>.does(command: (@BrigadierDsl CommandContext<S>) -> Int) = executes(command)

/**
 * Gets the value of a (required) argument in the command hierarchy
 *
 * @see CommandContext.getArgument
 */
inline infix fun <reified R, S> String.from(ctx: CommandContext<S>) = ctx.getArgument(this, R::class.java)

/**
 * Shorthand for [runs] with an empty [now] handler that always returns `0`.
 */
infix fun <T: Message> ArgumentBuilder<Cmd<T>, *>.runs(action: T.(CommandContext<Cmd<T>>) -> Unit) =
    does { context ->
        context.source runs {
            action(this, context)
        }
        0
    }

fun CommandNode<*>.getAllArguments(): ArrayList<String> {
    val list = ArrayList<String>()
    for (child in children) {
        for (arg in child.getAllArguments()) list.add("$usageText $arg")
    }
    if (list.isEmpty()) list.add(usageText)
    return list
}

fun minecraft.bot.Command<*>.getUsageList(): String {
    var text = "Usage: "
    if (arguments.isEmpty()) {
        text += literal
        return text
    }
    for (arg in arguments) text += arg.getAllArguments().joinToString(separator = "") { "\n - $literal $it" }
    return text
}