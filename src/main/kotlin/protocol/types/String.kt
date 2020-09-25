package protocol.types

import io.ktor.utils.io.*

@ExperimentalUnsignedTypes
suspend fun ByteReadChannel.readMinecraftString(): String {
    return readUTF8Line(readVarInt().value)?: ""
}

@ExperimentalUnsignedTypes
fun String.toMinecraftBytes(): UByteArray {
    val bytes = ArrayList<UByte>()
    this.encodeToByteArray().forEach { bytes.add(it.toUByte()) }
    VarInt(bytes.size).getBytes().reversedArray().forEach { bytes.add(0, it) }
    return bytes.toUByteArray()
}