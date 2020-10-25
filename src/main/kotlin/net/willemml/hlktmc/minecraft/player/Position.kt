package net.willemml.hlktmc.minecraft.player

import net.willemml.hlktmc.minecraft.world.types.BlockPos
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import kotlin.math.pow
import kotlin.math.roundToInt

data class Position(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
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
}

data class PositionDelta(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
    fun isZero() = x == 0.0 && y == 0.0 && z == 0.0

    fun distanceSquared() = x.pow(2) + y.pow(2) + z.pow(2)

    fun zero() {
        x = 0.0
        y = 0.0
        z = 0.0
    }

    companion object {
        fun from(a: Position, b: Position) = PositionDelta(b.x.minus(a.x), b.y.minus(a.y), b.z.minus(a.z))
    }
}