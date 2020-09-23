package protocol

import io.netty.buffer.ByteBuf

class Tag(private val type: Int = 0) {
    private val emptyArrayList = ArrayList<Tag>()
    private val emptyHashMap = HashMap<String, Tag>()
    private var content: Any? = null

    fun writeToBuffer(buffer: ByteBuf) {
        when (type) {
            0 -> return
            1 -> buffer.writeByte((content as Byte).toInt())
            2 -> buffer.writeShort((content as Short).toInt())
            3 -> buffer.writeShort(content as Int)
            4 -> buffer.writeLong(content as Long)
            5 -> buffer.writeFloat(content as Float)
            6 -> buffer.writeDouble(content as Double)
            7 -> {
                buffer.writeInt((content as ByteArray).size)
                buffer.writeBytes(content as ByteArray)
            }
            8 -> buffer.writeString(content as String)
            9 -> {
                if ((content as ArrayList<*>).isEmpty()) {
                    buffer.writeByte(0)
                    buffer.writeInt(0)
                }
                else {
                    buffer.writeByte(internalID())
                    buffer.writeInt((content as ArrayList<*>).size)
                    (content as ArrayList<*>).forEach { (it as Tag).writeToBuffer(buffer) }
                }
            }
            10 -> {
                (content as HashMap<*, *>).forEach {
                    buffer.writeByte((it.value as Tag).internalID())
                    buffer.writeString(it.key as String)
                    (it.value as Tag).writeToBuffer(buffer)
                }
                buffer.writeByte(0)
            }
            11 -> {
                buffer.writeInt((content as IntArray).size)
                (content as IntArray).forEach { buffer.writeInt(it) }
            }
            12 -> {
                buffer.writeInt((content as LongArray).size)
                (content as LongArray).forEach { buffer.writeLong(it) }
            }
        }
    }

    fun readFromBuffer(buffer: ByteBuf) {
        content = when (internalID()) {
            1 -> buffer.readByte()
            2 -> buffer.readShort()
            3 -> buffer.readInt()
            4 -> buffer.readLong()
            5 -> buffer.readFloat()
            6 -> buffer.readDouble()
            7 -> buffer.readBytes(buffer.readInt())
            8 -> buffer.readString()
            9 -> {
                val newList = ArrayList<Tag>()
                val readType = buffer.readByte().toInt()
                val length = buffer.readInt()
                if (readType != 0 && length != 0) {
                    val newTag = Tag(readType)
                    newTag.readFromBuffer(buffer)
                    newList.add(newTag)
                }
                newList
            }
            10 -> {
                val newHashMap = HashMap<String, Tag>()
                val readType = buffer.readByte().toInt()
                var nextByte = buffer.readByte()
                while (nextByte.toInt() != 0) {
                    val key = buffer.readString(nextByte)
                    val value = Tag(readType)
                    value.readFromBuffer(buffer)
                    newHashMap[key] = value
                    nextByte = buffer.readByte()
                }
            }
            11 -> {
                val size = buffer.readInt()
                val intArray = IntArray(size)
                for (i in (intArray.indices)) {
                    intArray[i] = buffer.readInt()
                }
                intArray
            }
            12 -> {
                val size = buffer.readInt()
                val longArray = LongArray(size)
                for (i in (longArray.indices)) {
                    longArray[i] = buffer.readLong()
                }
                longArray
            }
            else -> null
        }
    }

    private fun internalID(): Int {
        return when (content) {
            is Byte -> 1
            is Short -> 2
            is Int -> 3
            is Long -> 4
            is Float -> 5
            is Double -> 6
            is ByteArray -> 7
            is String -> 8
            isList() -> 9
            isMap() -> 10
            is IntArray -> 11
            is LongArray -> 12
            else -> 0
        }
    }

    private fun isList(): Boolean {
        if (content is ArrayList<*>) {
            val contentCopy = content as ArrayList<*>
            contentCopy.clear()
            if (contentCopy == emptyArrayList) {
                return true
            }
        }
        return false
    }

    private fun isMap(): Boolean {
        if (content is HashMap<*, *>) {
            val contentCopy = content as HashMap<*, *>
            contentCopy.clear()
            if (contentCopy == emptyHashMap) {
                return true
            }
        }
        return false
    }
}

fun ByteBuf.writeString(string: String) {
    val bytes = string.toByteArray(Charsets.UTF_8)
    this.writeShort(bytes.size)
    this.writeBytes(bytes)
}

fun ByteBuf.readString(first: Byte? = null): String {
    var bytes = readBytes(readShort().toInt()).array()
    if (first != null) {
        bytes = byteArrayOf(first).plus(bytes)
    }
    return bytes.toString(Charsets.UTF_8)
}