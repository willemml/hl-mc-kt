package protocol.types

import io.ktor.utils.io.*
import kotlin.experimental.or

suspend fun ByteWriteChannel.writeVarLong(value: Long) {
    do {
        var temp = (value and 127).toByte()
        val variableValue: Long = value ushr 7
        if (variableValue != 0L) {
            temp = temp or 128.toByte()
        }
        writeByte(temp)
    } while (variableValue != 0L)
}

suspend fun ByteReadChannel.readVarLong(): Long {
    var numRead = 0
    var result = 0L
    var read: Byte
    do {
        read = readByte()
        val value = read + 127
        result = result or ((value shl (7 * numRead)).toLong())
        numRead++
        if (numRead > 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while ((read + 128) != 0)
    return result
}