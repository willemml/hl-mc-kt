package protocol

import io.ktor.util.*
import protocol.types.writeVarInt
import java.net.Socket

@ExperimentalUnsignedTypes
@KtorExperimentalAPI
fun protocolTest() {
    val socket = Socket("localhost", 25565)
    println(socket.inetAddress.hostName)
    socket.getOutputStream().writeVarInt(751)
    socket.getOutputStream().write("localhost".encodeToByteArray())
    socket.getOutputStream().write(2)
    socket.getOutputStream().writeVarInt(2)
    socket.getOutputStream().flush()
    println("done writing")
    println(socket.getInputStream().readAllBytes().joinToString(","))
    /*println("test 1")
    buildPacket {
        writeVarInt(751)
        writeText("localhost")
        writeUShort((25565).toUShort())
        writeVarInt(2)
        println("test 2")
        val client = Socket("localhost", 25565).also{ println(it.inetAddress.hostName) }.also { it.getOutputStream().writePacket(build()) }.also { println(it.getInputStream().readAllBytes().joinToString(",")) }
    }*/
    //println(client.getInputStream().readAllBytes().joinToString(","))
    /*val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("localhost", 25565))
    val readChannel = socket.openReadChannel()
    val writeChannel = socket.openWriteChannel(true)
    /*val t: ByteArray = ByteArray(10000000)
    readChannel.readAvailable(t)
    println(t.joinToString(","))*/
    writeChannel.writeVarInt(751)
    writeChannel.writeStringUtf8("localhost")
    writeChannel.writeUShort(25565.toUShort())
    writeChannel.writeVarInt(NextState.Login.id)
    println(readChannel.readUTF8Line(32767))*/
}