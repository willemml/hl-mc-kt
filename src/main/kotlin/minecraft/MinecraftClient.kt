package minecraft

import br.com.devsrsouza.ktmcpacket.MinecraftProtocol
import br.com.devsrsouza.ktmcpacket.internal.MinecraftByteInput
import br.com.devsrsouza.ktmcpacket.internal.MinecraftByteOutput
import br.com.devsrsouza.ktmcpacket.packets.ClientPacket
import br.com.devsrsouza.ktmcpacket.packets.client.Handshake
import br.com.devsrsouza.ktmcpacket.packets.client.HandshakeNextState
import br.com.devsrsouza.ktmcpacket.packets.client.play.LoginStart
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import io.ktor.util.network.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import io.ktor.utils.io.jvm.javaio.*
import io.ktor.utils.io.streams.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import minecraft.mojang.Session
import java.net.SocketAddress

@KtorExperimentalAPI
class MinecraftClient(val session: Session, val serverAddress: SocketAddress, val protocolVersion: Int) {
    val protocol = MinecraftProtocol()
    var socket: Socket
    var input: ByteReadChannel
    var mcInput: MinecraftByteInput
    var output: ByteWriteChannel
    var mcOutput: MinecraftByteOutput

    init {
        runBlocking {
            socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(serverAddress)
            input = socket.openReadChannel()
            mcInput = MinecraftByteInput(input.toInputStream().asInput())
            output = socket.openWriteChannel()
            mcOutput = MinecraftByteOutput(output.toOutputStream().asOutput())
        }
    }

    @InternalSerializationApi
    suspend inline fun <reified T : ClientPacket> writePacket(id: Int, packetData: T) {
        val packet = BytePacketBuilder()
        packet.writeVarInt(id)
        protocol.dump(packet, T::class.serializer(), packetData)
        mcOutput.writeVarInt(packet.size)
        output.writeFully(packet.build().readBytes())
    }

    @InternalSerializationApi
    suspend inline fun <reified T : ClientPacket> readPacket(): T {
        return protocol.load(T::class.serializer(), input.readPacket(mcInput.readVarInt()))
    }

    @InternalSerializationApi
    suspend fun connect() {
        writePacket(0x00, Handshake(protocolVersion, serverAddress.hostname, serverAddress.port.toShort(), HandshakeNextState.LOGIN))
        writePacket(0x00, LoginStart(session.name))
    }
}

fun BytePacketBuilder.writeVarInt(value: Int) {
    MinecraftByteOutput(outputStream().asOutput()).writeVarInt(value)
}