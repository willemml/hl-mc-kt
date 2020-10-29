package net.willemml.hlktmc

import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
abstract class Config

@Serializable
data class ClientConfig(
        var address: String = "127.0.0.1",
        var port: Int = 25565,
        var username: String = randomAlphanumeric(16),
        var password: String = "",
        var chunkUnloadDistance: Int = 2, // chunks that are further from the player than this are unloaded or not loaded
        var logConnection: Boolean = true,
        var logRespawns: Boolean = true,
        var logChat: Boolean = true,
        var visibleParts: List<SkinPart> = listOf(SkinPart.CAPE, SkinPart.HAT, SkinPart.JACKET, SkinPart.LEFT_PANTS_LEG, SkinPart.LEFT_SLEEVE, SkinPart.RIGHT_PANTS_LEG, SkinPart.RIGHT_SLEEVE),
        var preferredHand: HandPreference = HandPreference.RIGHT_HAND,
        var chatVisibility: ChatVisibility = ChatVisibility.FULL,
        var locale: String = "en_US"
)

@Serializable
data class ChatBotConfig(
        var config: ClientConfig = ClientConfig(),
        var prefix: String = "!"
)

fun randomAlphanumeric(length: Int): String {
    return (1..length).map { alphanumeric[Random.nextInt(0, alphanumeric.length)] }.joinToString("")
}

const val alphanumeric = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"