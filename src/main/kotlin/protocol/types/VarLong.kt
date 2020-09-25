package protocol.types

import io.ktor.utils.io.*

class VarLong(var value: Long) {
    @ExperimentalUnsignedTypes
    fun getBytes(): UByteArray {
        val arrayList = ArrayList<UByte>()
        do {
            var temp = (value and 127).toUByte()
            value = value ushr 7
            if (value != 0L) {
                temp = temp or 128.toUByte()
            }
            arrayList.add(temp)
        } while (value != 0L)
        return arrayList.toUByteArray()
    }
}

@ExperimentalUnsignedTypes
suspend fun ByteReadChannel.toVarLong(): VarLong {
    var numRead = 0
    var result = 0L
    var read: UByte
    do {
        read = readUByte()
        val value = read + 127u
        result = result or ((value shl 7 * numRead).toLong())
        numRead++
        if (numRead > 10) {
            throw RuntimeException("VarLong is too big")
        }
    } while ((read + 128u) != 0u)
    return VarLong(result)
}