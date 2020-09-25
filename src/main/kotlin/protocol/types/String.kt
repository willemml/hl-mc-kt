package protocol.types

import io.ktor.utils.io.*

suspend fun ByteReadChannel.readMinecraftString(): String {
    return readUTF8Line(readVarInt())?: ""
}

fun String.toMinecraftBytes(): ByteArray {
    val bytes = ArrayList<Byte>()
    this.encodeToByteArray().forEach { bytes.add(it) }
    VarInt(bytes.size).getBytes().reversedArray().forEach { bytes.add(0, it) }
    return bytes.toByteArray()
}