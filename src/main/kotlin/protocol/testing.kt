package protocol

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import protocol.packets.client.handshake.NextState
import protocol.types.writeUShort
import protocol.types.writeVarInt
import java.net.InetSocketAddress

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
suspend fun protocolTest() {
    val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("localhost", 25565))
    val readChannel = socket.openReadChannel()
    val writeChannel = socket.openWriteChannel(true)
    writeChannel.writeVarInt(751)
    writeChannel.writeStringUtf8("localhost")
    writeChannel.writeUShort(25565.toUShort())
    writeChannel.writeVarInt(NextState.Login.id)
    println(readChannel.readUTF8Line(32767))
}