package minecraft.mojang

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*


val client = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = JacksonSerializer()
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

enum class URLs(val url: String) {
    Join("https://sessionserver.minecraft.mojang.com/session/minecraft/join"),
    Login("https://authserver.minecraft.mojang.com/authenticate"),
    Refresh("https://authserver.minecraft.mojang.com/refresh"),
    Validate("https://authserver.minecraft.mojang.com/validate"),
    SignOut("https://authserver.minecraft.mojang.com/signout")
}

suspend fun login(username: String, password: String): Profile {
    return client.post {
        url(URLs.Login.url)
        contentType(ContentType.Application.Json)
        body = LoginRequest(username, password)
    }
}

suspend fun refresh(profile: Profile): Profile {
    profile.accessToken = client.post<Profile> {
        url(URLs.Refresh.url)
        contentType(ContentType.Application.Json)
        body = Refresh(profile.accessToken, profile.clientToken)
    }.accessToken
    return profile
}

suspend fun validate(profile: Profile): Boolean {
    return client.post<HttpResponse> {
        url(URLs.Validate.url)
        contentType(ContentType.Application.Json)
        body = Validate(profile.accessToken, profile.clientToken)
    }.status == HttpStatusCode.NoContent
}

suspend fun signOut(username: String, password: String) {
    client.post<HttpResponse> {
        url(URLs.SignOut.url)
        contentType(ContentType.Application.Json)
        body = SignOut(username, password)
    }
}