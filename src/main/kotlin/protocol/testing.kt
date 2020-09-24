package protocol

import io.ktor.util.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.streams.*
import protocol.types.writeVarInt
import java.net.Socket

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
fun protocolTest() {
    val client = Socket("localhost", 25565)
    buildPacket {
        writeVarInt(751)
        writeText("localhost")
        writeUShort((25565).toUShort())
        writeVarInt(2)
        client.getOutputStream().writePacket(build())
        close()
    }
    println(client.getInputStream().readAllBytes().joinToString(","))
    /*val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("localhost", 25565))
    val readChannel = socket.openReadChannel()
    val writeChannel = socket.openWriteChannel(true)
    /*val t: ByteArray = ByteArray(10000000
    readChannel.readAvailable(t)
    println(t.joinToString(","))*/
    writeChannel.writeVarInt(751)
    writeChannel.writeStringUtf8("localhost")
    writeChannel.writeUShort(25565.toUShort())
    writeChannel.writeVarInt(NextState.Login.id)
    println(readChannel.readUTF8Line(32767))*/
}