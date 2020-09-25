package protocol.packets.server.handshake

import io.ktor.utils.io.*
import protocol.packets.server.ServerPacket
import protocol.types.VarInt
import protocol.types.readMinecraftString
import protocol.types.readVarInt

class Response : ServerPacket(VarInt(0x00)) {
    var jsonResponse: String = ""
    @ExperimentalUnsignedTypes
    override suspend fun readFrom(channel: ByteReadChannel): Response {
        println(channel.readVarInt())
        println(channel.readVarInt())
        jsonResponse = channel.readMinecraftString()
        return this
    }
}