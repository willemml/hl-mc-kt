package protocol.packets.client.handshake

import protocol.packets.client.ClientPacket
import protocol.types.VarInt
import protocol.types.toMinecraftBytes


enum class NextState(val id: VarInt) {
    Status(VarInt(1)),
    Login(VarInt(2))
}

data class Handshake @ExperimentalUnsignedTypes constructor(
    val protocolVersion: VarInt,
    val serverAddress: String,
    val serverPort: UShort,
    val nextState: NextState,
) : ClientPacket(VarInt(0x00)) {
    @ExperimentalUnsignedTypes
    override suspend fun toByteArray(): UByteArray {
        val bytes = ArrayList<UByte>()
        protocolVersion.getBytes().forEach { bytes.add(it) }
        serverAddress.toMinecraftBytes().forEach { bytes.add(it) }
        nextState.id.getBytes().forEach { bytes.add(it) }
        return bytes.toUByteArray()
    }
}