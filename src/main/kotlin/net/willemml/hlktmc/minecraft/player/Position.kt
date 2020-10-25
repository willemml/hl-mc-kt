package net.willemml.hlktmc.minecraft.player

import net.willemml.hlktmc.minecraft.world.types.BlockPos
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
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

    override fun equals(other: Any?): Boolean {
        return if (other is Position) other.x == x && other.y == y && other.z == z else false
    }

    override operator fun compareTo(other: Position): Int {
        val distance = PositionDelta(x, y, z).distanceSquared()
        val distanceOther = PositionDelta(other.x, other.y, other.z).distanceSquared()
        if (distance > distanceOther) return 1
        if (distance < distanceOther) return -1
        return 0
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}

data class PositionDelta(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
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

    companion object {
        fun from(a: Position, b: Position) = PositionDelta(b.x.minus(a.x), b.y.minus(a.y), b.z.minus(a.z))
    }
}