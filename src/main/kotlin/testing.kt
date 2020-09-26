
import br.com.devsrsouza.ktmcpacket.MinecraftProtocol
import br.com.devsrsouza.ktmcpacket.internal.minecraft
import br.com.devsrsouza.ktmcpacket.packets.client.Handshake
import br.com.devsrsouza.ktmcpacket.packets.client.HandshakeNextState
import br.com.devsrsouza.ktmcpacket.packets.client.status.Ping
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.jvm.javaio.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import java.net.InetSocketAddress

@InternalSerializationApi
@ExperimentalUnsignedTypes
@KtorExperimentalAPI
fun protocolTest() {
    runBlocking {
        val protocol = MinecraftProtocol()
        val byteArray: ByteArray = protocol.dump(Handshake.serializer(), Handshake(751, "mc.blazenarchy.net", 25565, HandshakeNextState.STATUS))
        val client = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 25565))
        println("client socket created")
        val input = ByteChannel()
        val output = ByteChannel(true)
        client.attachForReading(input)
        client.attachForWriting(output)
        println("read write channels opened")
        output.toOutputStream().asOutput().minecraft.writeVarInt(byteArray.size)
        output.writeFully(byteArray)
        protocol.load(Ping.serializer(), input.readPacket(input.toInputStream().asInput().minecraft.readVarInt()))
    }
}