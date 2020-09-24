package protocol

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.asCoroutineDispatcher
import protocol.packets.client.handshake.NextState
import protocol.types.writeUShort
import protocol.types.writeVarInt
import java.net.InetSocketAddress
import java.util.concurrent.Executors

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
suspend fun protocolTest() {val exec = Executors.newCachedThreadPool()
    val selector = ActorSelectorManager(exec.asCoroutineDispatcher())
    val tcpSocketBuilder = aSocket(selector).tcp()
    val client = tcpSocketBuilder.connect(InetSocketAddress("127.0.0.1", 25565))
    println("client socket created")
    val readChannel = client.openReadChannel()
    val writeChannel = client.openWriteChannel(true)
    println("read write channels opened")
    writeChannel.writeVarInt(751)
    writeChannel.writeStringUtf8("localhost")
    writeChannel.writeUShort(25565.toUShort())
    writeChannel.writeVarInt(NextState.Login.id)
    println("data sent?")
    println(readChannel.readUTF8Line(32767))
}