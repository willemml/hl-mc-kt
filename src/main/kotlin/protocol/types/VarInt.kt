package protocol.types

import io.ktor.utils.io.core.*
import java.io.OutputStream
import kotlin.experimental.or

fun OutputStream.writeVarInt(value: Int) {
    var bytes = ByteArray(0)
    do {
        var temp = (value and 127).toByte()
        val variableValue: Int = value ushr 7
        if (variableValue != 0) {
            temp = temp or 128.toByte()
        }
        bytes += temp
    } while (variableValue != 0)
    write(bytes)
}

fun ByteReadPacket.readVarInt(): Int {
    var numRead = 0
    var result = 0
    var read: Byte
    do {
        read = readByte()
        val value: Int = read + 127
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while ((read + 128) != 0)
    return result
}