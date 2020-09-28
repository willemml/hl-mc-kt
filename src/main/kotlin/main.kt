import io.ktor.util.*
import kotlinx.serialization.InternalSerializationApi
import minecraft.clientTest

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
@InternalSerializationApi
fun main() {
    server()
    clientTest()
}