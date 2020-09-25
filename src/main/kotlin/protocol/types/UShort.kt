package protocol.types

@ExperimentalUnsignedTypes
fun ByteArray.readUShort(): UShort {
    return (((this[0].toUInt() and 255u) shl 8) or (this[1].toUInt() and 255u)).toUShort()
}

@ExperimentalUnsignedTypes
fun UShort.shr(bitCount: Int): UShort {
    return toUInt().shr(bitCount).toUShort()
}

@ExperimentalUnsignedTypes
fun UShort.shl(bitCount: Int): UShort {
    return toUInt().shl(bitCount).toUShort()
}

@ExperimentalUnsignedTypes
fun UShort.toByteArray(isBigEndian: Boolean = true): ByteArray {
    var bytes = byteArrayOf()

    var n = this

    val o: UShort = 0x00u

    if (n == o) {
        bytes += n.toByte()
    } else {
        while (n != o) {
            val b = n.toByte()

            bytes += b
            n = n.shr(Byte.SIZE_BITS)
        }
    }

    val padding = o.toByte()
    var paddings = byteArrayOf()
    repeat(UShort.SIZE_BYTES - bytes.count()) {
        paddings += padding
    }

    return if (isBigEndian) {
        paddings + bytes.reversedArray()
    } else {
        paddings + bytes
    }
}