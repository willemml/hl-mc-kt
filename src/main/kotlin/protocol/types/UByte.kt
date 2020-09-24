package protocol.types

import io.ktor.utils.io.*

@ExperimentalUnsignedTypes
suspend fun ByteWriteChannel.writeUByte(value: UByte) {
    writeByte(value.toByte())
}

suspend fun ByteReadChannel.readBit(): Int {
    return if (readBoolean()) 1 else 0
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