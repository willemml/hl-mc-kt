package protocol

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*

enum class URLs(url: String) {
    Join("https://sessionserver.mojang.com/session/minecraft/join"),
    Login("https://authserver.mojang.com/authenticate"),
    Refresh("https://authserver.mojang.com/refresh"),
    Validate("https://authserver.mojang.com/validate")
}

fun login(username: String, password: String) {
    val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
    }
}