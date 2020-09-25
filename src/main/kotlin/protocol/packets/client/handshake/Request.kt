package protocol.packets.client.handshake

import protocol.packets.client.ClientPacket
import protocol.types.VarInt

class Request: ClientPacket(VarInt(0x00)) {
    @ExperimentalUnsignedTypes
    override suspend fun toByteArray(): UByteArray = UByteArray(0)
}