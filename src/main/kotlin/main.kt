import cli.CLI
import io.ktor.util.*
import kotlinx.serialization.InternalSerializationApi
import minecraft.clientTest
import kotlin.random.Random

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
@InternalSerializationApi
fun main() {
    server()
    clientTest()
    CLI()
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}