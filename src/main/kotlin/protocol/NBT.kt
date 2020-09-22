package protocol

import io.netty.buffer.ByteBuf

data class Tag(val content: Any? = null) {
    fun writeToBuffer(buffer: ByteBuf) {
        when (content) {
            null -> return
            is Byte -> buffer.writeByte(content.toInt())
            is Short -> buffer.writeShort(content.toInt())
            is Int -> buffer.writeShort(content)
            is Long -> buffer.writeShort(content.toInt())
            is Float -> buffer.writeShort(content.toInt())
            is Double -> buffer.writeShort(content.toInt())
            is ByteArray -> buffer.writeBytes(content)
            is String -> buffer.writeCharSequence(content, Charsets.UTF_8)
            is List<*> -> {
                if (content.isNotEmpty()) {
                    when (content[0]) {
                        is Int -> {
                            content.forEach { buffer.writeInt(it as Int) }
                        }
                        is Double -> {
                            content.forEach { buffer.writeInt((it as Double).toInt()) }
                        }
                         is List<*> -> {
                            if ((content[0] as List<*>).isNotEmpty()) {
                                if ((content[0] as List<*>)[0] is Tag) {
                                    (content as List<List<Tag>>).forEach { it.forEach { it.writeToBuffer(buffer) } }
                                    return
                                }
                            }
                            buffer.writeByte(0)
                        }
                    }
                }
            }
            is Map<*, *> -> {
                if (content.isNotEmpty()) {
                    if (content.keys.first() is String && content.values.first() is Tag) {
                        content.forEach {
                            buffer.writeCharSequence(it.key as String, Charsets.UTF_8)
                            (it.value as Tag).writeToBuffer(buffer)
                        }
                    }
                }
                buffer.writeByte(0)
            }
        }
    }
}

data class NamedTag(var name: String, var tag: Tag)