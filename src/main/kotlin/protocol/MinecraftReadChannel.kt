package protocol

import io.ktor.utils.io.*
import kotlin.experimental.and

class MinecraftReadChannel(val readChannel: ByteReadChannel) {
    suspend fun readString(maxLength: Int): String {
        val i: Int = readVarInt()
        return if (i > maxLength * 4) throw RuntimeException("String is more than 32767 bytes long.")
        else if (i < 1) throw RuntimeException("String is less than 1 byte long.")
        else {
            val string = readChannel.readUTF8Line(i) ?: throw RuntimeException("String is null")
            if (string.length > maxLength) throw RuntimeException("String is too long")
            else string
        }
    }

    suspend fun readVarLong(): Long {
        var numRead = 0
        var result = 0L
        var read: Byte
        do {
            read = readChannel.readByte()
            val value = (read and 127).toLong()
            result = result or (value shl 7 * numRead)
            numRead++
            if (numRead > 10) {
                throw RuntimeException("VarInt is too big")
            }
        } while (read and 128.toByte() != 0.toByte())
        return result
    }

    suspend fun readVarInt(): Int {
        var numRead = 0
        var result = 0
        var read: Byte
        do {
            read = readChannel.readByte()
            val value = (read and 127).toInt()
            result = result or (value shl 7 * numRead)
            numRead++
            if (numRead > 5) {
                throw RuntimeException("VarInt is too big")
            }
        } while (read and 128.toByte() != 0.toByte())
        return result
    }
}