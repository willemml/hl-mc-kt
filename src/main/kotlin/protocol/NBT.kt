// file from https://gist.github.com/camdenorrb/bec73c5608267f0232bd8f5c42e0784d

/*
MIT License

Copyright (c) 2020 Camden B

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

// package me.camdenorrb.afancehmcserverimpl.nmc

package protocol

import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.DeflaterInputStream
import java.util.zip.DeflaterOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

// TODO: Add coroutine support
class NBT(
    val name: String,
    map: Map<String, Any>,
    internal val listIDs: MutableMap<String, Int> = mutableMapOf()
) {

    internal val map = map.toMutableMap()


    operator fun <T> invoke(key: String): Lazy<T> {
        return lazy { get(key) }
    }

    fun <T> get(key: String): T {
        return map[key] as T
    }


    fun pushZlib(output: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN) {
        push(StreamedOutput(DataOutputStream(DeflaterOutputStream(output.outputStream().buffered())), endianness))
    }

    fun pushGzip(output: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN) {
        push(StreamedOutput(DataOutputStream(GZIPOutputStream(output.outputStream().buffered())), endianness))
    }

    fun push(output: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN) {

        output.parentFile?.mkdirs()
        output.createNewFile()

        push(output.outputStream(), endianness)
    }

    fun push(output: DataOutputStream, endianness: ByteOrder = ByteOrder.BIG_ENDIAN) {

        val nbtSize = getNBTSize()
        val byteBuffer = ByteBuffer.allocateDirect(nbtSize).apply { order(endianness) }

        push(byteBuffer)

        val bytes = ByteArray(nbtSize).apply {
            byteBuffer.get(0, this)
        }

        output.write(bytes)
    }

    fun push(output: FileOutputStream, endianness: ByteOrder = ByteOrder.BIG_ENDIAN) {

        val byteBuffer = ByteBuffer.allocateDirect(getNBTSize()).apply { order(endianness) }
        push(byteBuffer)

        byteBuffer.position(0)

        output.channel.write(byteBuffer)
    }

    fun push(output: ByteBuffer) {
        push(ByteBufferOutput(output))
    }

    fun push(output: Output) {
        if (output is AutoCloseable) {
            output.use { write(output, name, this) }
        }
        else {
            write(output, name, this)
        }
    }

    // Gets the total amount of bytes needed to serialize this
    fun getNBTSize(): Int {
        return getTagSize(name, this)
    }


    override fun toString(): String {

        return map.entries.joinToString("\n", "{\n", "\n}") { (name, value) ->

            val valueText = when (value) {
                is IntArray  -> value.contentToString()
                is ByteArray -> value.contentToString()
                is LongArray -> value.contentToString()
                is String  -> "\"$value\""
                is List<*> -> "\n${"$value".prependIndent("  ")}"
                else -> "$value"
            }

            "[${value::class.simpleName}] $name = $valueText".prependIndent("  ")
        }
    }

    override fun equals(other: Any?): Boolean {
        return toString() == other.toString()
    }

    override fun hashCode(): Int {

        var result = name.hashCode()
        result = 31 * result + listIDs.hashCode()

        map.forEach { (key, value) ->

            result = 31 * result + key.hashCode()

            result = 31 * result + when (value) {
                is IntArray  -> value.contentHashCode()
                is ByteArray -> value.contentHashCode()
                is LongArray -> value.contentHashCode()
                else -> value.hashCode()
            }
        }

        return result
    }


    // Gets the total amount of bytes needed to serialize this
    private fun getTagSize(key: String, value: Any): Int {

        // Key Size Short Size + Key Size + TagID Size + Value Size
        return Short.SIZE_BYTES + key.length + Byte.SIZE_BYTES + when (value) {

            is Byte -> Byte.SIZE_BYTES
            is Short -> Short.SIZE_BYTES
            is Int, is Float -> Int.SIZE_BYTES
            is Long, is Double -> Long.SIZE_BYTES
            is String -> Short.SIZE_BYTES + value.encodeToByteArray().size

            // Size of List + List Type ID Size
            is List<*> -> Int.SIZE_BYTES + Byte.SIZE_BYTES + value.sumBy {
                // Doesn't need Key Size Short Size nor TagID hence minus
                getTagSize("", it as Any) - Short.SIZE_BYTES - Byte.SIZE_BYTES
            }

            is NBT -> {

                val dataSize = value.map.entries.sumBy {
                    getTagSize(it.key, it.value)
                }

                // Map Size + End Tag Size
                dataSize + Byte.SIZE_BYTES
            }

            is ByteArray -> Int.SIZE_BYTES + value.size
            is IntArray -> Int.SIZE_BYTES + value.size * Int.SIZE_BYTES
            is LongArray -> Int.SIZE_BYTES + value.size * Long.SIZE_BYTES

            else -> error("Unknown tag: [${value::class.simpleName}]")
        }
    }


    // Push without ID
    private fun write(output: Output, name: String?, value: Any) {

        when (value) {

            is Byte -> output.writeByte(value)
            is Short -> output.writeShort(value)
            is Int -> output.writeInt(value)
            is Long -> output.writeLong(value)
            is Float -> output.writeFloat(value)
            is Double -> output.writeDouble(value)

            is ByteArray -> {
                output.writeInt(value.size)
                output.writeByteArray(value)
            }


            is String -> output.writeUTF8(value)

            is List<*> -> {

                // Defaults to tag end type
                val listId = listIDs[name]!!

                output.writeByte(listId.toByte())
                output.writeInt(value.size)

                value.forEach {
                    write(output, null, it!!)
                }
            }

            is NBT -> {

                // If not in a list
                if (name != null) {
                    output.writeByte(10)   // Compound ID
                    output.writeUTF8(name) // Compound Name
                }

                value.map.forEach { (name, value) ->

                    val id = idFor(value)

                    // Is not compound
                    if (id != 10) {
                        output.writeByte(id.toByte())
                        output.writeUTF8(name)
                    }

                    write(output, name, value)
                }

                // End tag
                output.writeByte(0)
            }

            is IntArray -> {
                output.writeInt(value.size)
                value.forEach { output.writeInt(it) }
            }

            is LongArray -> {
                output.writeInt(value.size)
                value.forEach { output.writeLong(it) }
            }

            else -> error("Unknown tag: [${value::class.simpleName}] $name = $value")
        }
    }

    private fun idFor(value: Any) = when(value) {

        is Byte -> 1
        is Short -> 2
        is Int -> 3
        is Long -> 4
        is Float -> 5
        is Double -> 6
        is ByteArray -> 7
        is String -> 8
        is List<*> -> 9
        is NBT -> 10
        is IntArray -> 11
        is LongArray -> 12

        else -> error("Unknown tag: [${value::class.simpleName}] $name = $value")
    }


    companion object {

        fun pull(input: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): NBT {
            return pull(input.readBytes(), endianness)
        }

        // Don't forget to close yourself
        // Provide a buffered stream please <3
        fun pull(input: ByteArray, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): NBT {
            return pull(ByteBuffer.wrap(input).apply { order(endianness) })
        }

        fun pull(input: ByteBuffer): NBT {
            return pull(ByteBufferInput(input))
        }

        fun pull(input: Input): NBT {
            return if (input is AutoCloseable) {
                input.use { read(input) }
            }
            else {
                read(input)
            }
        }

        fun pull(input: DataInputStream, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): NBT {
            return read(StreamedInput(input, endianness))
        }

        fun pullGzip(input: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): NBT {
            return pull(DataInputStream(GZIPInputStream(input.inputStream().buffered())), endianness)
        }

        fun pullZlib(input: File, endianness: ByteOrder = ByteOrder.BIG_ENDIAN): NBT {
            return pull(DataInputStream(DeflaterInputStream(input.inputStream().buffered())), endianness)
        }


        private fun read(input: Input, readID: Boolean = true, name: String? = null): NBT {

            if (readID) {
                check(input.readByte() == 10.toByte()) {
                    "Expected a compound, didn't get one :C"
                }
            }

            val nbt = NBT(name ?: input.readUTF8(), mapOf())


            while (true) {

                val inID = input.readByte().toInt()

                val inName = if (inID != 0) input.readUTF8() else ""

                // Is end
                if (inID == 0) {
                    break
                }

                nbt.map[inName] = readTag(input, inID, nbt, inName)
            }

            return nbt
        }

        private fun readList(name: String, nbt: NBT, input: Input): List<*> {

            val inID = input.readByte().toInt()
            val size = input.readInt()

            nbt.listIDs[name] = inID

            return List(size) {
                readTag(input, inID, nbt, "")
            }
        }

        private fun readTag(input: Input, id: Int, nbt: NBT, name: String?): Any = when(id) {

            1 -> input.readByte()
            2 -> input.readShort()
            3 -> input.readInt()
            4 -> input.readLong()
            5 -> input.readFloat()
            6 -> input.readDouble()
            7 -> input.readByteArray(input.readInt())
            8 -> input.readUTF8()
            9 -> readList(name!!, nbt, input)
            10 -> read(input, false, name)
            11 -> IntArray(input.readInt()) { input.readInt() }
            12 -> LongArray(input.readInt()) { input.readLong() }

            else -> error("Invalid NBT id: $id")
        }

        private fun Input.readUTF8(): String {
            return readByteArray(readShort().toInt()).decodeToString()
        }

        private fun Output.writeUTF8(text: String) {
            val byteArray = text.encodeToByteArray()
            writeShort(byteArray.size.toShort())
            writeByteArray(byteArray)
        }
    }



    interface Input {
        fun readByte(): Byte
        fun readShort(): Short
        fun readInt(): Int
        fun readLong(): Long
        fun readFloat(): Float
        fun readDouble(): Double
        fun readByteArray(amountOfBytes: Int): ByteArray
    }

    interface Output {
        fun writeByte(byte: Byte)
        fun writeShort(short: Short)
        fun writeInt(int: Int)
        fun writeLong(long: Long)
        fun writeFloat(float: Float)
        fun writeDouble(double: Double)
        fun writeByteArray(byteArray: ByteArray) // Does not prepend the size
    }

    // TODO: Simplify the conversion of bytes by looking at LittleEndianDataInputStream's implementation
    // TODO: Use extensions for above's TODO
    class StreamedInput(private val inputStream: DataInputStream, private val endianness: ByteOrder) : InputStream(), Input {

        // Needed for InputStream inheritance
        override fun read(): Int {
            return inputStream.read()
        }

        override fun readByte(): Byte {
            return inputStream.readByte()
        }

        override fun readShort(): Short {
            return if (endianness == ByteOrder.LITTLE_ENDIAN) {
                java.lang.Short.reverseBytes(inputStream.readShort())
            }
            else {
                return inputStream.readShort()
            }
        }

        override fun readInt(): Int {
            return if (endianness == ByteOrder.LITTLE_ENDIAN) {
                Integer.reverseBytes(inputStream.readInt())
            }
            else {
                inputStream.readInt()
            }
        }

        override fun readLong(): Long {
            return if (endianness == ByteOrder.LITTLE_ENDIAN) {
                java.lang.Long.reverseBytes(inputStream.readLong())
            }
            else {
                inputStream.readLong()
            }
        }

        override fun readFloat(): Float {
            return if (endianness == ByteOrder.LITTLE_ENDIAN) {
                java.lang.Float.intBitsToFloat(Integer.reverseBytes(java.lang.Float.floatToIntBits(inputStream.readFloat())))
            }
            else {
                inputStream.readFloat()
            }
        }

        override fun readDouble(): Double {
            return if (endianness == ByteOrder.LITTLE_ENDIAN) {
                java.lang.Double.longBitsToDouble(java.lang.Long.reverseBytes(java.lang.Double.doubleToLongBits(inputStream.readDouble())))
            }
            else {
                inputStream.readDouble()
            }
        }

        override fun readByteArray(amountOfBytes: Int): ByteArray {
            return inputStream.readNBytes(amountOfBytes)
        }

        override fun close() {
            inputStream.close()
        }

    }

    class ByteBufferInput(private val byteBuffer: ByteBuffer) : Input {

        override fun readByte(): Byte {
            return byteBuffer.get()
        }

        override fun readShort(): Short {
            return byteBuffer.short
        }

        override fun readInt(): Int {
            return byteBuffer.int
        }

        override fun readLong(): Long {
            return byteBuffer.long
        }

        override fun readFloat(): Float {
            return byteBuffer.float
        }

        override fun readDouble(): Double {
            return byteBuffer.double
        }

        // Needs to be done this way to support endianness
        override fun readByteArray(amountOfBytes: Int): ByteArray {
            return ByteArray(amountOfBytes).apply {
                byteBuffer.get(this)
            }
        }

    }

    class StreamedOutput(private val outputStream: DataOutputStream, private val endianness: ByteOrder) : OutputStream(), Output {

        // Needed for OutputStream inheritance
        override fun write(b: Int) {
            return outputStream.write(b)
        }

        override fun writeByte(byte: Byte) {
            outputStream.write(byte.toInt())
        }

        override fun writeShort(short: Short) {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                outputStream.writeShort(java.lang.Short.reverseBytes(short).toInt())
            }
            else {
                outputStream.writeShort(short.toInt())
            }
        }

        override fun writeInt(int: Int) {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                outputStream.writeInt(Integer.reverseBytes(int))
            }
            else {
                outputStream.writeInt(int)
            }
        }

        override fun writeLong(long: Long) {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                outputStream.writeLong(java.lang.Long.reverseBytes(long))
            }
            else {
                outputStream.writeLong(long)
            }
        }

        override fun writeFloat(float: Float) {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                outputStream.writeFloat(java.lang.Float.intBitsToFloat(Integer.reverseBytes(java.lang.Float.floatToIntBits(float))))
            }
            else {
                outputStream.writeFloat(float)
            }
        }

        override fun writeDouble(double: Double) {
            if (endianness == ByteOrder.LITTLE_ENDIAN) {
                outputStream.writeDouble(java.lang.Double.longBitsToDouble(java.lang.Long.reverseBytes(java.lang.Double.doubleToLongBits(double))))
            }
            else {
                outputStream.writeDouble(double)
            }
        }

        override fun writeByteArray(byteArray: ByteArray) {
            outputStream.write(byteArray)
        }


        override fun close() {
            outputStream.close()
        }

    }

    class ByteBufferOutput(private val byteBuffer: ByteBuffer) : Output {

        override fun writeByte(byte: Byte) {
            byteBuffer.put(byte)
        }

        override fun writeShort(short: Short) {
            byteBuffer.putShort(short)
        }

        override fun writeInt(int: Int) {
            byteBuffer.putInt(int)
        }

        override fun writeLong(long: Long) {
            byteBuffer.putLong(long)
        }

        override fun writeFloat(float: Float) {
            byteBuffer.putFloat(float)
        }

        override fun writeDouble(double: Double) {
            byteBuffer.putDouble(double)
        }

        override fun writeByteArray(byteArray: ByteArray) {
            byteBuffer.put(byteArray)
        }

    }

}