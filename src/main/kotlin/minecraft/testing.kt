package minecraft

import com.github.steveice10.mc.protocol.MinecraftProtocol
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

fun protocolTest() {
    println("Connecting...")
    val client = Client("127.0.0.1", 25565, MinecraftProtocol("test"), TcpSessionFactory())
    client.session.addListener(object : SessionAdapter() {
        override fun connected(event: ConnectedEvent?) {
            println("Connected.")
        }

        override fun packetReceived(event: PacketReceivedEvent?) {
            when (event?.getPacket<Packet>()) {
                is ServerJoinGamePacket -> event.session.send(ClientChatPacket("Hello, testing MCProtocolLib in hl-mc-kt!"))
                is ServerChatPacket -> println("Server says: \"${event.getPacket<ServerChatPacket>().message}\".")
            }
        }

        override fun disconnected(event: DisconnectedEvent?) {
            println("Disconnected: ${event?.reason}")
        }
    })
    client.session.connect()
}