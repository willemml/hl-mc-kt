package net.willemml.hlktmc.minecraft.world.types

import net.willemml.hlktmc.minecraft.player.Position
import kotlin.math.roundToInt


data class BlockPos(val x: Int, val y: Int, val z: Int) {
    fun position() = Position(x.toDouble(), y.toDouble(), z.toDouble())

    fun posInChunk() = BlockPos(x % 16, y % 16, z % 16)

    fun chunkPos() = ChunkPos(x / 16, z / 16, y / 16)
}