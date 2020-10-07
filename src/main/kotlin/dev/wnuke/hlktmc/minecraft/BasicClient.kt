package dev.wnuke.hlktmc.minecraft

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
import kotlinx.coroutines.delay
import dev.wnuke.hlktmc.randomAlphanumeric
import java.util.*
import kotlin.collections.HashMap

data class ClientConfig(
    var address: String = "127.0.0.1",
    var port: Int = 25565,
    var protocol: MinecraftProtocol = MinecraftProtocol(randomAlphanumeric(8)),
    var tcpSessionFactory: TcpSessionFactory = TcpSessionFactory(),
    var connectionLogs: Boolean = true,
    var chatLogs: Boolean = true
)

open class BasicClient(val config: ClientConfig = ClientConfig()) {
    private val client = Client(config.address, config.port, config.protocol, config.tcpSessionFactory)

    private var joined = false

    private val hostPort = "${config.address}:${config.port}"

    var player: Player? = null

    var playerListHeader = ""
    var playerListFooter = ""
    val playerListEntries = HashMap<UUID, GameProfile>()

    init {
        client.session.addListener(object : SessionAdapter() {
            override fun connected(event: ConnectedEvent?) {
                if (config.connectionLogs) println("$hostPort Connected.")
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
                            player?.position?.x = player?.position?.x?.plus(packet.moveX / (128 * 32))!!
                            player?.position?.y = player?.position?.y?.plus(packet.moveX / (128 * 32))!!
                            player?.position?.z = player?.position?.z?.plus(packet.moveX / (128 * 32))!!
                        }
                    }
                    is ServerEntityRotationPacket -> {
                        val packet = event.getPacket<ServerEntityRotationPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position?.pitch = player?.position?.pitch?.plus(packet.pitch)!!
                            player?.position?.yaw = player?.position?.yaw?.plus(packet.yaw)!!
                        }
                    }
                    is ServerEntityPositionRotationPacket -> {
                        val packet = event.getPacket<ServerEntityPositionRotationPacket>()
                        if (packet.entityId == player?.entityID) {
                            player?.position?.x = player?.position?.x?.plus(packet.moveX / (128 * 32))!!
                            player?.position?.y = player?.position?.y?.plus(packet.moveX / (128 * 32))!!
                            player?.position?.z = player?.position?.z?.plus(packet.moveX / (128 * 32))!!
                            player?.position?.pitch = player?.position?.pitch?.plus(packet.pitch)!!
                            player?.position?.yaw = player?.position?.yaw?.plus(packet.yaw)!!
                        }
                    }
                    is ServerChatPacket -> {
                        respawn()
                        val packet = event.getPacket<ServerChatPacket>()
                        if (packet.type == MessageType.CHAT) {
                            val message = MessageSerializer.toJson(packet.message).asJsonObject.getAsJsonArray("with")
                                .last().asString
                            if (packet.senderUuid != config.protocol.profile.id) {
                                if (config.chatLogs) println("[$hostPort]${packet.senderUuid} > $message")
                                onChat(message, packet.senderUuid)
                            }
                        }
                    }
                    is ServerCombatPacket -> {
                        respawn()
                    }
                    is ServerSpawnPlayerPacket -> {
                        val packet = event.getPacket<ServerSpawnPlayerPacket>()
                        if (packet.uuid == config.protocol.profile.id) {
                            player = Player(
                                config.protocol.profile.name,
                                packet.uuid,
                                packet.entityId,
                                Position(packet.x, packet.y, packet.z, packet.pitch, packet.yaw)
                            )
                        }
                    }
                    is ServerPlayerListEntryPacket -> {
                        val packet = event.getPacket<ServerPlayerListEntryPacket>()
                        for (entry in packet.entries) {
                            playerListEntries[entry.profile.id] = entry.profile
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
                if (config.connectionLogs) println("$hostPort Disconnected, reason: ${event?.reason}")
                joined = false
                onLeave(event ?: return)
            }
        })
    }

    suspend fun connect(): BasicClient {
        if (config.connectionLogs) println("$hostPort Connecting...")
        client.session.connect()
        while (!joined) {
            delay(5)
        }
        return this
    }

    fun disconnect(): BasicClient {
        if (config.connectionLogs) println("$hostPort Disconnecting...")
        client.session.disconnect("Leaving...")
        return this
    }

    fun respawn(): BasicClient {
        client.session.send(ClientRequestPacket(ClientRequest.RESPAWN))
        return this
    }

    fun sendMessage(message: String): BasicClient {
        client.session.send(ClientChatPacket(message))
        return this
    }

    open fun onJoin(packet: ServerJoinGamePacket) {}
    open fun onLeave(event: DisconnectedEvent) {}
    open fun onChat(message: String, sender: UUID) {}
}

data class Player(val name: String, val uuid: UUID, var entityID: Int, var position: Position)

data class Position(var x: Double, var y: Double, var z: Double, var pitch: Float, var yaw: Float)