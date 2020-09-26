package protocol

import io.ktor.utils.io.*
import protocol.packets.Packet
import protocol.packets.VarInt
import protocol.packets.client.handshake.NextState
import kotlin.experimental.or
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

class MinecraftWriteChannel(val writeChannel: ByteWriteChannel) {
    @ExperimentalUnsignedTypes
    suspend inline fun <reified T : Packet> writePacket(packet: T) {
        println(packet::class.memberProperties.joinToString { it.name })
        for (property in packet::class.memberProperties) {
            if (property.name == "id") continue
            when (property.returnType) {
                Int::class.starProjectedType -> println("${property.name} is Int")
                String::class.starProjectedType -> println("${property.name} is String")
                UShort::class.starProjectedType -> println("${property.name} is UShort")
                NextState::class.starProjectedType -> println("${property.name} is NextState")
                else -> println(property.name)
            }
            if (property.hasAnnotation<VarInt>()) println("${property.name} is VarInt")
        }
    }

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