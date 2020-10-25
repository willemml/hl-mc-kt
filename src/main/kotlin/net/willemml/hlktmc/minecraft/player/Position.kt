package net.willemml.hlktmc.minecraft.player

import net.willemml.hlktmc.minecraft.world.types.BlockPos
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

data class Position(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) : Comparable<Position> {
    private var modifying = false

    fun addDelta(delta: PositionDelta) {
        x += delta.x
        y += delta.y
        z += delta.z
    }

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun set(position: Position) {
        set(position.x, position.y, position.z)
    }

    fun blockPos() = BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())

    fun chunkPos() = ChunkPos((x / 16).roundToInt(), (z / 16).roundToInt(), (y / 16).roundToInt())

    override operator fun compareTo(other: Position): Int {
        val distance = PositionDelta.from(Position(), this)
        val distanceOther = PositionDelta.from(Position(), other)
        return when {
            distance > distanceOther -> 1
            distance < distanceOther -> -1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (modifying != other.modifying) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + modifying.hashCode()
        return result
    }
}

data class PositionDelta(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) : Comparable<PositionDelta> {
    fun isZero() = x == 0.0 && y == 0.0 && z == 0.0

    fun distanceSquared(): Double {
        return when {
            x == 0.0 && y == 0.0 -> z
            x == 0.0 && z == 0.0 -> y
            z == 0.0 && y == 0.0 -> x
            else -> x.pow(2) + y.pow(2) + z.pow(2)
        }
    }

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    override operator fun compareTo(other: PositionDelta): Int {
        if (this == other) return 0
        val array = arrayListOf(abs(x), abs(y), abs(z)).apply { sort() }
        val arrayOther = arrayListOf(abs(other.x), abs(other.y), abs(other.z)).apply { sort() }
        return when {
            array[0] > arrayOther[0] -> 1
            array[0] < arrayOther[0] -> -1
            array[1] > arrayOther[1] -> 1
            array[1] < arrayOther[1] -> -1
            array[2] > arrayOther[2] -> 1
            array[2] < arrayOther[2] -> -1
            else -> 0
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PositionDelta

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    companion object {
        fun from(a: Position, b: Position) = PositionDelta(b.x.minus(a.x), b.y.minus(a.y), b.z.minus(a.z))
    }
}