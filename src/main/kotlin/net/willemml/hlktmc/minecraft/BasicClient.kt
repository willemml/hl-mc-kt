package net.willemml.hlktmc.minecraft

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.ClientRequest
import com.github.steveice10.mc.protocol.data.game.MessageType
import com.github.steveice10.mc.protocol.data.game.chunk.Column
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement
import com.github.steveice10.mc.protocol.data.message.MessageSerializer
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.*
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.*
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateViewPositionPacket
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.ConnectedEvent
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import kotlinx.coroutines.delay
import net.daporkchop.lib.minecraft.text.component.MCTextRoot
import net.daporkchop.lib.minecraft.text.parser.AutoMCFormatParser
import net.daporkchop.lib.minecraft.text.util.TranslationSource
import net.willemml.hlktmc.ClientConfig
import java.util.*
import kotlin.ConcurrentModificationException
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

    var player = Player(protocol.profile.name, protocol.profile.id ?: UUID.randomUUID())
    var positionDelta = PositionDelta()
    var rotationDelta = RotationDelta()

    val chunks = HashMap<ChunkPosition, Column>()

    var timer = Timer()

    val ticker = object : TimerTask() {
        override fun run() {
            if (joined) {
                try {
                    val yDecimal = player.position.y - player.position.y.toInt()
                    val isBlockUnderSolid = isBlockSolid(player.position.copy(y = player.position.y.minus(1)))
                    if (yDecimal != 0.0 || isBlockUnderSolid == false) {
                        if (yDecimal >= 0.1 || (isBlockUnderSolid == false && yDecimal == 0.0)) positionDelta.deltaY -= 0.1
                        else positionDelta.deltaY -= yDecimal
                    }
                    if (!positionDelta.isZero() || !rotationDelta.isZero()) checkMovement()
                } catch (_: ConcurrentModificationException) {
                }
            }
        }
    }

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
                        val packet = event.getPacket<ServerJoinGamePacket>()
                        player.entityID = packet.entityId
                        player.gameMode = packet.gameMode
                        sendClientSettings()
                        joined = true
                        timer.schedule(ticker, 0, 50)
                        onJoin(packet)
                    }
                    is LoginSuccessPacket -> {
                        val packet = event.getPacket<LoginSuccessPacket>()
                        player = Player(packet.profile.name, packet.profile.id)
                    }
                    is ServerSpawnPositionPacket -> {
                        val position = event.getPacket<ServerSpawnPositionPacket>().position
                        player.spawnPoint = Position(position.x.toDouble(), position.y.toDouble(), position.z.toDouble())
                    }
                    is ServerPlayerPositionRotationPacket -> {
                        val packet = event.getPacket<ServerPlayerPositionRotationPacket>()
                        if (packet.relative.contains(PositionElement.X)) player.position.x += packet.x
                        else player.position.x = packet.x
                        if (packet.relative.contains(PositionElement.Y)) player.position.y += packet.y
                        else player.position.y = packet.y
                        if (packet.relative.contains(PositionElement.Z)) player.position.z += packet.z
                        else player.position.z = packet.z
                        if (packet.relative.contains(PositionElement.YAW)) player.rotation.yaw += packet.yaw
                        else player.rotation.yaw = packet.yaw
                        if (packet.relative.contains(PositionElement.PITCH)) player.rotation.pitch += packet.pitch
                        else player.rotation.pitch = packet.pitch
                        client.session.send(ClientTeleportConfirmPacket(packet.teleportId))
                    }
                    is ServerPlayerHealthPacket -> {
                        val packet = event.getPacket<ServerPlayerHealthPacket>()
                        player.health.health = packet.health
                        player.health.saturation = packet.saturation
                        player.health.food = packet.food
                        if (player.health.health <= 0) respawn()
                    }
                    is ServerUpdateViewPositionPacket -> {
                        val packet = event.getPacket<ServerUpdateViewPositionPacket>()
                        player.chunk = ChunkPosition(packet.chunkX, packet.chunkZ)
                        for (chunk in chunks.keys) {
                            if (chunk.x > packet.chunkX + config.chunkUnloadDistance ||
                                    chunk.x < packet.chunkX - config.chunkUnloadDistance ||
                                    chunk.z < packet.chunkZ - config.chunkUnloadDistance ||
                                    chunk.z > packet.chunkZ + config.chunkUnloadDistance
                            ) {
                                chunks.remove(chunk)
                            }
                        }
                    }
                    is ServerChunkDataPacket -> {
                        val packet = event.getPacket<ServerChunkDataPacket>()
                        if (!(packet.column.x > player.chunk.x + config.chunkUnloadDistance ||
                                        packet.column.x < player.chunk.x - config.chunkUnloadDistance ||
                                        packet.column.z < player.chunk.z - config.chunkUnloadDistance ||
                                        packet.column.z > player.chunk.z + config.chunkUnloadDistance
                                        )) {
                            chunks[ChunkPosition(packet.column.x, packet.column.z)] = packet.column
                        }
                    }
                    is ServerEntityTeleportPacket -> {
                        val packet = event.getPacket<ServerEntityTeleportPacket>()
                        if (packet.entityId == player.entityID) {
                            player.position = Position(packet.x, packet.y, packet.z)
                            player.rotation = Rotation(packet.yaw, packet.pitch)
                            player.onGround = packet.isOnGround
                        }
                    }
                    is ServerEntityPositionPacket -> {
                        val packet = event.getPacket<ServerEntityPositionPacket>()
                        if (packet.entityId == player.entityID) {
                            player.position.addDelta(PositionDelta(
                                    packet.moveX / (128 * 32),
                                    packet.moveX / (128 * 32),
                                    packet.moveX / (128 * 32)
                            ))
                        }
                    }
                    is ServerEntityRotationPacket -> {
                        val packet = event.getPacket<ServerEntityRotationPacket>()
                        if (packet.entityId == player.entityID) {
                            player.rotation.set(packet.yaw, packet.pitch)
                        }
                    }
                    is ServerEntityPositionRotationPacket -> {
                        val packet = event.getPacket<ServerEntityPositionRotationPacket>()
                        if (packet.entityId == player.entityID) {
                            player.position.addDelta(PositionDelta(
                                    packet.moveX / (128 * 32),
                                    packet.moveX / (128 * 32),
                                    packet.moveX / (128 * 32)
                            ))
                            player.rotation.set(packet.yaw, packet.pitch)
                        }
                        player.rotation
                    }
                    is ServerChatPacket -> {
                        val packet = event.getPacket<ServerChatPacket>()
                        val message = parser.parse(MessageSerializer.toJsonString(packet.message))
                        val messageString = message.toRawString()
                        if (config.logChat) logChat(messageString, packet.type, packet.senderUuid, message)
                        onChat(messageString, packet.type, packet.senderUuid, message)
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
                timer.cancel()
                timer = Timer()
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

    private fun checkMovement() {
        val previousPosition = player.position
        player.position.addDelta(positionDelta)
        positionDelta.zero()
        player.rotation.addDelta(rotationDelta)
        rotationDelta.zero()
        if (isBlockSolid(player.position) == true) player.position = previousPosition
        client.session.send(ClientPlayerPositionRotationPacket(true,
                player.position.x.plus(positionDelta.deltaX),
                player.position.y.plus(positionDelta.deltaY),
                player.position.z.plus(positionDelta.deltaZ),
                player.rotation.yaw.plus(rotationDelta.yawDelta),
                player.rotation.pitch.plus(rotationDelta.pitchDelta)
        ))
    }

    private fun isBlockSolid(position: Position): Boolean? {
        val chunkPos = ChunkPosition.fromPosition(position)
        if (chunkPos.y > chunks.size) return true
        val column = chunks[chunkPos.copy(y = 0)] ?: return null
        val chunk = column.chunks[chunkPos.y] ?: return true
        val positionInChunk = chunkPos.getChunkPosition(position)
        try {
            val block = chunk.get(positionInChunk.x.toInt(), positionInChunk.y.toInt(), positionInChunk.z.toInt())
            return block != 0
        } catch (_: IndexOutOfBoundsException) {
            return true
        }
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

    fun sendClientSettings() {
        client.session.send(ClientSettingsPacket(config.locale, config.chunkUnloadDistance, config.chatVisibility, false, config.visibleParts, config.preferredHand))
    }

    fun changeChunkDistance(newDistance: Int) {
        config.chunkUnloadDistance = newDistance
        sendClientSettings()
    }

    fun getNameFromID(id: UUID) = playerListEntries[id]?.name

    open fun logChat(message: String, messageType: MessageType, sender: UUID, rawMessage: MCTextRoot) {
        println("${getNameFromID(sender) ?: sender}@$hostPort ($messageType): $message")
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

data class Player(val name: String,
                  val uuid: UUID,
                  var entityID: Int = 0,
                  var onGround: Boolean = true,
                  var position: Position = Position(),
                  var chunk: ChunkPosition = ChunkPosition(),
                  var rotation: Rotation = Rotation(),
                  var spawnPoint: Position = Position(),
                  var health: Health = Health(),
                  var gameMode: GameMode = GameMode.SPECTATOR
)

data class Health(var health: Float = 0.0f,
                  var saturation: Float = 0.0f,
                  var food: Int = 0
)

