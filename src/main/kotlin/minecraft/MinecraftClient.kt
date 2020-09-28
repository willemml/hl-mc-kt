package minecraft

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.MessageType
import com.github.steveice10.mc.protocol.data.message.MessageSerializer
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.ConnectedEvent
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import kotlinx.coroutines.delay
import randomAlphanumeric
import java.util.*

open class MinecraftClient(
    val address: String,
    val port: Int = 25565,
    val protocol: MinecraftProtocol = MinecraftProtocol(randomAlphanumeric(8)),
    tcpSessionFactory: TcpSessionFactory = TcpSessionFactory(),
    var connectionLogs: Boolean = true,
    var chatLogs: Boolean = true
) {
    private val client = Client(address, port, protocol, tcpSessionFactory)

    private var joined = false

    init {
        client.session.addListener(object : SessionAdapter() {
            override fun connected(event: ConnectedEvent?) {
                if (connectionLogs) println("$address:$port Connected.")
            }

            override fun packetReceived(event: PacketReceivedEvent?) {
                when (event?.getPacket<Packet>()) {
                    is ServerJoinGamePacket -> {
                        joined = true
                        onJoin(event.getPacket())
                    }
                    is ServerChatPacket -> {
                        val packet = event.getPacket<ServerChatPacket>()
                        if (packet.type == MessageType.CHAT) {
                            val message = MessageSerializer.toJson(packet.message).asJsonObject.getAsJsonArray("with").last().asString
                            if (packet.senderUuid != protocol.profile.id) {
                                if (chatLogs) println("[$address:$port]${packet.senderUuid} > $message")
                                onChat(message, packet.senderUuid)
                            }
                        }
                    }
                }
            }

            override fun disconnected(event: DisconnectedEvent?) {
                if (connectionLogs) println("$address:$port Disconnected, reason: ${event?.reason}")
                joined = false
                onLeave(event ?: return)
            }
        })
    }

    suspend fun connect(): MinecraftClient {
        if (connectionLogs) println("$address:$port Connecting...")
        client.session.connect()
        while (!joined) {
            delay(5)
        }
        println(client.session.flags.asIterable().joinToString(", "))
        return this
    }

    fun disconnect(): MinecraftClient {
        if (connectionLogs) println("$address:$port Disconnecting...")
        client.session.disconnect("Leaving...")
        return this
    }

    fun sendMessage(message: String): MinecraftClient {
        client.session.send(ClientChatPacket(message))
        return this
    }

    open fun onJoin(packet: ServerJoinGamePacket) {}
    open fun onLeave(event: DisconnectedEvent) {}
    open fun onChat(message: String, sender: UUID) {}
}

class MessageEvent(val client: MinecraftClient, val message: String, val sender: UUID)