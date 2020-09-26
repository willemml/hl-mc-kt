package protocol.packets.client.handshake

import protocol.packets.VarInt
import protocol.packets.client.ClientPacket


enum class NextState(@VarInt val id: Int) {
    Status(1),
    Login(2)
}

data class Handshake @ExperimentalUnsignedTypes constructor(
    @VarInt val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: UShort,
    val nextState: NextState,
) : ClientPacket(0x00)