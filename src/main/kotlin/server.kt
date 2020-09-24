
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import kotlinx.html.*
import protocol.protocolTest

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

@KtorExperimentalAPI
@ExperimentalUnsignedTypes
suspend fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
        }
    }.start(wait = false)
    protocolTest()
}