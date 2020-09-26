package protocol

import io.ktor.util.*
import io.ktor.utils.io.*
import protocol.packets.Packet
import protocol.packets.VarInt
import protocol.packets.VarLong
import protocol.packets.client.handshake.NextState
import java.util.*
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.UShort
import kotlin.experimental.or
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.starProjectedType

class MinecraftWriteChannel(val writeChannel: ByteWriteChannel) {
    @ExperimentalUnsignedTypes
    suspend fun <T : Packet> writePacket(packet: T) {
        val channel = ByteChannel()
        channel.writeVarInt(packet.id)
        val parameters = packet::class.primaryConstructor?.parameters?.filterIsInstance<KMutableProperty<*>>()
        if (parameters != null) {
            println(parameters.joinToString { it.name })
            for (property in parameters) {
                if (property.name == "id") continue
                val value = property.getter.call()
                when (property.returnType) {
                    Int::class.starProjectedType -> {
                        when {
                            property.hasAnnotation<VarInt>() -> channel.writeVarInt(value as Int)
                            else -> channel.writeInt(value as Int)
                        }
                    }
                    Long::class.starProjectedType -> {
                        when {
                            property.hasAnnotation<VarLong>() -> channel.writeVarLong(value as Long)
                            else -> channel.writeLong(value as Long)
                        }
                    }
                    String::class.starProjectedType -> channel.writeString(value as String)
                    UShort::class.starProjectedType -> channel.writeShort((value as UShort).toInt())
                    NextState::class.starProjectedType -> channel.writeVarInt((value as NextState).id)
                    NBT::class.starProjectedType -> (value as NBT).push(channel)
                    Boolean::class.starProjectedType -> channel.writeBoolean(value as Boolean)
                    Byte::class.starProjectedType -> channel.writeByte(value as Byte)
                    UByte::class.starProjectedType -> channel.writeByte((value as UByte).toInt())
                    Short::class.starProjectedType -> channel.writeShort(value as Short)
                    Float::class.starProjectedType -> channel.writeFloat(value as Float)
                    Double::class.starProjectedType -> channel.writeDouble(value as Double)
                    UUID::class.starProjectedType -> {
                        val uuid = value as UUID
                        channel.writeLong(uuid.leastSignificantBits)
                        channel.writeLong(uuid.mostSignificantBits)
                    }
                }
            }
        }
        writeChannel.writeFully(ByteChannel().apply { writeVarInt(channel.toByteArray().size) }
            .apply { writeFully(channel.toByteArray()) }.toByteArray())
    }
}

suspend fun ByteChannel.writeString(string: String) {
    val bytes = string.toByteArray()
    if (bytes.size > Short.MAX_VALUE) throw RuntimeException("String is over 32767 bytes long.")
    else {
        writeVarInt(bytes.size)
        writeFully(bytes)
    }
}

suspend fun ByteChannel.writeVarLong(varLong: Long) {
    var varValue = varLong
    do {
        var temp = (varValue and 127).toByte()
        varValue = varValue ushr 7
        if (varValue != 0L) {
            temp = temp or 128.toByte()
        }
        writeByte(temp)
    } while (varValue != 0L)
}

suspend fun ByteChannel.writeVarInt(varInt: Int) {
    var varValue = varInt
    do {
        var temp = (varValue and 127).toByte()
        varValue = varValue ushr 7
        if (varValue != 0) {
            temp = temp or 128.toByte()
        }
        writeByte(temp)
    } while (varValue != 0)
}