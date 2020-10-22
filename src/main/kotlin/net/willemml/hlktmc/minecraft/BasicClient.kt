package net.willemml.hlktmc.minecraft

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.ClientRequest
import com.github.steveice10.mc.protocol.data.game.MessageType
import com.github.steveice10.mc.protocol.data.message.MessageSerializer
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.*
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.ConnectedEvent
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import net.willemml.hlktmc.ClientConfig
import kotlinx.coroutines.delay
import net.daporkchop.lib.minecraft.text.component.MCTextRoot
import net.daporkchop.lib.minecraft.text.parser.AutoMCFormatParser
import net.daporkchop.lib.minecraft.text.util.TranslationSource
import java.util.*
import kotlin.collections.HashMap

open class BasicClient(val config: ClientConfig = ClientConfig()) {
    private val protocol: MinecraftProtocol =
        if (config.password.isEmpty()) MinecraftProtocol(config.username) else MinecraftProtocol(
            config.username,
            config.password
        )

    private val client = Client(config.address, config.port, protocol, TcpSessionFactory())

    private var joined = false

    private val hostPort = "${config.address}:${config.port}"

    private val parser = AutoMCFormatParser(TranslationSource.ofMap(hashMapOf(Pair("chat.type.text", "<%s> %s"), Pair("chat.type.announcement", "[%s] %s"))))

    var player: Player? = null

    var playerListHeader = ""
    var playerListFooter = ""
    val playerListEntries = HashMap<UUID, GameProfile>()

    init {
        client.session.addListener(object : SessionAdapter() {
            override fun connected(event: ConnectedEvent?) {
                if (config.logConnection) connectionLog("", ConnectionLogType.CONNECTED)
            }

            override fun packetReceived(event: PacketReceivedEvent?) {
                when (event?.getPacket<Packet>()) {
                    is ServerJoinGamePacket -> {
                        respawn()
                        joined = true
                        onJoin(event.getPacket())
                    }
                    is ServerEntityTeleportPacket -> {
                        val packet = event.getPacket<ServerEntityTeleportPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position = Position(packet.x, packet.y, packet.z, packet.pitch, packet.yaw)
                        }
                    }
                    is ServerEntityPositionPacket -> {
                        val packet = event.getPacket<ServerEntityPositionPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position?.x = player?.position?.x?.plus(packet.moveX / (128 * 32)) ?: return
                            player?.position?.y = player?.position?.y?.plus(packet.moveX / (128 * 32)) ?: return
                            player?.position?.z = player?.position?.z?.plus(packet.moveX / (128 * 32)) ?: return
                        }
                    }
                    is ServerEntityRotationPacket -> {
                        val packet = event.getPacket<ServerEntityRotationPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position?.pitch = player?.position?.pitch?.plus(packet.pitch) ?: return
                            player?.position?.yaw = player?.position?.yaw?.plus(packet.yaw) ?: return
                        }
                    }
                    is ServerEntityPositionRotationPacket -> {
                        val packet = event.getPacket<ServerEntityPositionRotationPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position?.x = player?.position?.x?.plus(packet.moveX / (128 * 32)) ?: return
                            player?.position?.y = player?.position?.y?.plus(packet.moveX / (128 * 32)) ?: return
                            player?.position?.z = player?.position?.z?.plus(packet.moveX / (128 * 32)) ?: return
                            player?.position?.pitch = player?.position?.pitch?.plus(packet.pitch) ?: return
                            player?.position?.yaw = player?.position?.yaw?.plus(packet.yaw) ?: return
                        }
                    }
                    is ServerChatPacket -> {
                        val packet = event.getPacket<ServerChatPacket>()
                        val message = parser.parse(MessageSerializer.toJsonString(packet.message))
                        val messageString = message.toRawString()
                        if (config.logChat) logChat(messageString, packet.type, packet.senderUuid, message)
                        onChat(messageString, packet.type, packet.senderUuid, message)
                    }
                    is ServerCombatPacket -> {
                        respawn()
                    }
                    is ServerSpawnPlayerPacket -> {
                        val packet = event.getPacket<ServerSpawnPlayerPacket>()
                        if (packet.uuid == protocol.profile.id) {
                            player = Player(
                                protocol.profile.name,
                                packet.uuid,
                                packet.entityId,
                                Position(packet.x, packet.y, packet.z, packet.pitch, packet.yaw)
                            )
                        }
                    }
                    is ServerPlayerListEntryPacket -> {
                        val packet = event.getPacket<ServerPlayerListEntryPacket>()
                        for (entry in packet.entries) {
                            if (entry.profile.isComplete) playerListEntries[entry.profile.id] = entry.profile
                        }
                    }
                    is ServerPlayerListDataPacket -> {
                        val packet = event.getPacket<ServerPlayerListDataPacket>()
                        playerListHeader = packet.header.toString()
                        playerListFooter = packet.header.toString()
                    }
                }
            }

            override fun disconnected(event: DisconnectedEvent?) {
                if (config.logConnection) connectionLog(event?.reason ?: "".let {
                    parser.parse(it).toRawString()
                }, ConnectionLogType.DISCONNECTED)
                joined = false
                onLeave(event ?: return)
            }
        })
    }

    suspend fun connect(): BasicClient {
        if (config.logConnection) connectionLog("", ConnectionLogType.CONNECTING)
        client.session.connect()
        while (!joined) {
            delay(5)
        }
        return this
    }

    fun disconnect(): BasicClient {
        if (config.logConnection) connectionLog("", ConnectionLogType.DISCONNECTING)
        client.session.disconnect("")
        return this
    }

    fun respawn(): BasicClient {
        client.session.send(ClientRequestPacket(ClientRequest.RESPAWN))
        if (config.logRespawns && joined) connectionLog("", ConnectionLogType.RESPAWN)
        return this
    }

    fun sendMessage(message: String): BasicClient {
        client.session.send(ClientChatPacket(message))
        return this
    }

    fun getNameFromID(id: UUID) = playerListEntries[id]?.name

    open fun logChat(message: String, messageType: MessageType, sender: UUID, rawMessage: MCTextRoot) {
        println("${getNameFromID(sender)?: sender}@$hostPort ($messageType): $message")
    }

    open fun connectionLog(info: String, type: ConnectionLogType) {
        when (type) {
            ConnectionLogType.DISCONNECTED -> {
                if (info.isNotEmpty()) println("$hostPort Disconnected, reason: $info")
                else println("$hostPort Disconnected")
            }
            ConnectionLogType.DISCONNECTING -> println("$hostPort Disconnecting")
            ConnectionLogType.CONNECTED -> println("$hostPort Connected")
            ConnectionLogType.RESPAWN -> println("$hostPort Respawned")
            ConnectionLogType.CONNECTING -> println("$hostPort Connecting")
        }

    }

    open fun onJoin(packet: ServerJoinGamePacket) {}
    open fun onLeave(event: DisconnectedEvent) {}
    open fun onChat(message: String, messageType: MessageType, sender: UUID, rawMessage: MCTextRoot) {}
}

enum class ConnectionLogType {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    DISCONNECTING,
    RESPAWN
}

data class Player(val name: String, val uuid: UUID, var entityID: Int, var position: Position)

data class Position(var x: Double, var y: Double, var z: Double, var pitch: Float, var yaw: Float)
