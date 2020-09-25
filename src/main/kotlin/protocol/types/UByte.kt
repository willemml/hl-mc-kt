package protocol.types

import io.ktor.utils.io.*

suspend fun ByteReadChannel.readBit(): Int {
    return if (readBoolean()) 1 else 0
}

@ExperimentalUnsignedTypes
suspend fun ByteWriteChannel.writeUByte(uByte: UByte) {
    if (uByte.toByte().toInt() == uByte.toInt()) {
        writeByte(uByte.toByte())
    } else {
        writeByte(uByte.toInt().toByte())
    }
}

@ExperimentalUnsignedTypes
suspend fun ByteReadChannel.readUByte(): UByte {
    return (readBit() + (readBit() * 2) + (readBit() * 4) + (readBit() * 8)).toUByte()
}

@ExperimentalUnsignedTypes
fun UByte.shr(bitCount: Int): UByte {
    return toUInt().shr(bitCount).toUByte()
}

@ExperimentalUnsignedTypes
fun UByte.shl(bitCount: Int): UByte {
    return toUInt().shl(bitCount).toUByte()
}