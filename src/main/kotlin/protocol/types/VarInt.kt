package protocol.types

import io.ktor.utils.io.*

class VarInt(var value: Int) {
    @ExperimentalUnsignedTypes
    fun getBytes(): UByteArray {
        val arrayList = ArrayList<UByte>()
        do {
            var temp = (value and 127).toUByte()
            value = value ushr 7
            if (value != 0) {
                temp = temp or 128.toUByte()
            }
            arrayList.add(temp)
        } while (value != 0)
        return arrayList.toUByteArray()
    }
}

@ExperimentalUnsignedTypes
suspend fun ByteReadChannel.readVarInt(): VarInt {
    var numRead = 0
    var result = 0
    var read: UByte
    do {
        read = readUByte()
        val value = read + 127u
        result = result or ((value shl 7 * numRead).toInt())
        numRead++
        if (numRead >= 5) {
            throw RuntimeException("VarInt is too big")
        }
    } while ((read + 128u) != 0u)
    return VarInt(result)
}