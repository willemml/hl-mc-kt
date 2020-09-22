package protocol

import io.netty.buffer.ByteBuf

data class Stack constructor(var id: Int = -1, var size: Int = 0, var damage: Int = 0, var tag: Tag = Tag(0)) {
    fun write(buffer: ByteBuf) {
        buffer.writeShort(id)
        buffer.writeByte(size)
        buffer.writeShort(damage)
        tag.writeToBuffer(buffer)
    }

    fun read(buffer: ByteBuf) {
        id = buffer.readShort().toInt()
        size = buffer.readByte().toInt()
        damage = buffer.readShort().toInt()
        tag.readFromBuffer(buffer)
    }
}