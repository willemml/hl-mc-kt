package net.willemml.hlktmc.minecraft.player

import net.willemml.hlktmc.minecraft.world.types.BlockPos
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import kotlin.math.pow
import kotlin.math.roundToInt

data class Position(var x: Double = 0.0, var y: Double = 0.0, var z: Double = 0.0) {
    fun addDelta(delta: PositionDelta) {
        x += delta.deltaX
        y += delta.deltaY
        z += delta.deltaZ
    }

    fun set(x: Double, y: Double, z: Double) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun blockPos() = BlockPos(this.x.toInt(), this.y.toInt(), this.z.toInt())

    fun chunkPos() = ChunkPos((x / 16).roundToInt(), (z / 16).roundToInt(), (y / 16).roundToInt())
}

data class PositionDelta(var deltaX: Double = 0.0, var deltaY: Double = 0.0, var deltaZ: Double = 0.0) {
    fun isZero() = deltaX == 0.0 && deltaY == 0.0 && deltaZ == 0.0

    fun distanceSquared() = deltaX.pow(2) + deltaY.pow(2) + deltaZ.pow(2)

    fun zero() {
        deltaX = 0.0
        deltaY = 0.0
        deltaZ = 0.0
    }

    companion object {
        fun from(a: Position, b: Position) = PositionDelta(b.x.minus(a.x), b.y.minus(a.y), b.z.minus(a.z))
    }
}