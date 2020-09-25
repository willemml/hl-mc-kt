package protocol.packets.client

import io.ktor.utils.io.*
import protocol.packets.Packet
import protocol.types.VarInt
import protocol.types.writeUByte

abstract class ClientPacket(id: VarInt): Packet(id) {
    @ExperimentalUnsignedTypes
    suspend fun send(channel: ByteWriteChannel) {
        val bytes = ArrayList<UByte>()
        id.getBytes().forEach { bytes.add(it) }
        this.toByteArray().forEach { bytes.add(it) }
        VarInt(bytes.size).getBytes().reversedArray().forEach { bytes.add(0, it) }
        bytes.forEach { channel.writeUByte(it) }
    }
    @ExperimentalUnsignedTypes
    abstract suspend fun toByteArray(): UByteArray
}