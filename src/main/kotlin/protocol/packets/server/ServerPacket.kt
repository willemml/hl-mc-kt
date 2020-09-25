package protocol.packets.server

import io.ktor.utils.io.*
import protocol.packets.Packet
import protocol.types.VarInt

abstract class ServerPacket(id: VarInt): Packet(id) {
    abstract suspend fun readFrom(channel: ByteReadChannel): ServerPacket
}