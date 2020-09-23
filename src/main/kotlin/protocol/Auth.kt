package protocol

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val client = HttpClient(Apache) {
    install(JsonFeature) {
        serializer = JacksonSerializer()
    }
}

enum class URLs(val url: String) {
    Join("https://sessionserver.mojang.com/session/minecraft/join"),
    Login("https://authserver.mojang.com/authenticate"),
    Refresh("https://authserver.mojang.com/refresh"),
    Validate("https://authserver.mojang.com/validate"),
    SignOut("https://authserver.mojang.com/signout")
}

suspend fun login(username: String, password: String): Profile {
    return client.post {
        url(URLs.Login.url)
        body = LoginRequest(username, password)
    }
}

suspend fun refresh(profile: Profile): Profile {
    profile.accessToken = client.post<Profile> {
        url(URLs.Refresh.url)
        body = Refresh(profile.accessToken, profile.clientToken)
    }.accessToken
    return profile
}

suspend fun validate(profile: Profile): Boolean {
    return client.post<HttpResponse> {
        url(URLs.Validate.url)
        body = Validate(profile.accessToken, profile.clientToken)
    }.status == HttpStatusCode.NoContent
}

suspend fun signOut(username: String, password: String) {
    client.post<HttpResponse> {
        url(URLs.SignOut.url)
        body = SignOut(username, password)
    }
}