package dev.wnuke.hlktmc

import dev.wnuke.hlktmc.cli.CLI
import kotlinx.serialization.InternalSerializationApi
import kotlin.random.Random

@ExperimentalUnsignedTypes
@InternalSerializationApi
fun main(args: Array<String>) {
    readDiscordConfig()
    readMinecraftConfig()
    if (args[0] == "file") {
        startDiscordBots()
        startMinecraftBots()
    }
    CLI()
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}