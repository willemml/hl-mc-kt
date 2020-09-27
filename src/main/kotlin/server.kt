
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import io.ktor.util.*
import kotlinx.html.*
import kotlinx.serialization.InternalSerializationApi
import minecraft.protocolTest

fun HTML.index() {
    head {
        title("hl-mc-kt")
    }
    body {
        div {
            +"Hello from Ktor in hl-mc-kt!"
        }
    }
}

@InternalSerializationApi
@KtorExperimentalAPI
@ExperimentalUnsignedTypes
fun main() {
    embeddedServer(Jetty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
        }
    }.start(wait = false)
    protocolTest()
}