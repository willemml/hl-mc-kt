package protocol

import io.ktor.utils.io.*
import kotlin.experimental.or

class MinecraftWriteChannel(val writeChannel: ByteWriteChannel) {
    suspend fun writeString(string: String) {
        val bytes = string.toByteArray()
        if (bytes.size > Short.MAX_VALUE) throw RuntimeException("String is over 32767 bytes long.")
        else {
            writeVarInt(bytes.size)
            writeChannel.writeFully(bytes)
        }
    }

    suspend fun writeVarInt(varInt: Int) {
        var varValue = varInt
        do {
            var temp = (varValue and 127).toByte()
            varValue = varValue ushr 7
            if (varValue != 0) {
                temp = temp or 128.toByte()
            }
            writeChannel.writeByte(temp)
        } while (varValue != 0)
    }
}