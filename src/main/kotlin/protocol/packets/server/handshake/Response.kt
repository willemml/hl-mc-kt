package protocol.packets.server.handshake

import io.ktor.utils.io.*
import protocol.packets.server.ServerPacket
import protocol.types.VarInt
import protocol.types.readMinecraftString

class Response : ServerPacket(VarInt(0x00)) {
    var jsonResponse: String = ""
    override suspend fun readFrom(channel: ByteReadChannel): Response {
        jsonResponse = channel.readMinecraftString()
        return this
    }
}