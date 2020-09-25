package protocol.packets.client.handshake

import protocol.packets.client.ClientPacket
import protocol.types.VarInt

class Request: ClientPacket(VarInt(0x00)) {
    override suspend fun toByteArray(): ByteArray = ByteArray(0)
}