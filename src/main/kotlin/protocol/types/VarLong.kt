package protocol.types

import io.ktor.utils.io.*
import kotlin.experimental.or

class VarLong(var value: Long) {
    fun getBytes(): ByteArray {
        val arrayList = ArrayList<Byte>()
        do {
            var temp = (value and 127).toByte()
            val variableValue: Long = value ushr 7
            if (variableValue != 0L) {
                temp = temp or 128.toByte()
            }
            arrayList.add(temp)
        } while (variableValue != 0L)
        return arrayList.toByteArray()
    }
}

suspend fun ByteReadChannel.toVarLong(): VarLong {
    var numRead = 0
    var result = 0L
    var read: Byte
    do {
        read = readByte()
        val value: Long = read + 127L
        result = result or (value shl 7 * numRead)
        numRead++
        if (numRead > 10) {
            throw RuntimeException("VarLong is too big")
        }
    } while ((read + 128) != 0)
    return VarLong(result)
}