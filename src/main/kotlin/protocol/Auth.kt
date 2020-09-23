package protocol

enum class URLs(url: String) {
    Join("https://sessionserver.mojang.com/session/minecraft/join"),
    Login("https://authserver.mojang.com/authenticate"),
    Refresh("https://authserver.mojang.com/refresh"),
    Validate("https://authserver.mojang.com/validate")
}

fun login(username: String, password: String) {

}