import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import kotlinx.html.*

fun HTML.index() {
    head {
        title("hl-mc-kt")
    }
    body {
        div {
            +"Hello from Ktor in hl-mc-kt!"
            +"This will eventually be a REST API, possibly also a web UI."
            button(name = "print") {
                +"Click Me!"
            }
        }
    }
}

fun server() {
    embeddedServer(Jetty, port = 8080, host = "127.0.0.1") {
        routing {
            get("/") {
                call.respondHtml(HttpStatusCode.OK, HTML::index)
            }
        }
    }.start(wait = false)
}