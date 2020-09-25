package protocol.packets.client

import io.ktor.utils.io.*
import protocol.packets.Packet
import protocol.types.VarInt

abstract class ClientPacket(id: VarInt): Packet(id) {
    suspend fun send(channel: ByteWriteChannel) {
        val bytes = ArrayList<Byte>()
        id.getBytes().forEach { bytes.add(it) }
        this.toByteArray().forEach { bytes.add(it) }
        VarInt(bytes.size).getBytes().reversedArray().forEach { bytes.add(0, it) }
        channel.writeFully(bytes.toByteArray())
    }
    abstract suspend fun toByteArray(): ByteArray
}