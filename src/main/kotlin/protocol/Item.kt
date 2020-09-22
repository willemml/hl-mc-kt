package protocol

import io.netty.buffer.ByteBuf

data class Stack constructor(val id: Int = -1, var size: Int = 0, var damage: Int = 0, var tag: Int = 0) {
    fun write(buffer: ByteBuf) {
        buffer.writeShort(id)
        buffer.writeByte(size)
        buffer.writeShort(damage)
    }
}