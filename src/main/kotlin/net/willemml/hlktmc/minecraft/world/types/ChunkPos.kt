package net.willemml.hlktmc.minecraft.world.types

import net.willemml.hlktmc.minecraft.player.Position
import kotlin.math.roundToInt

data class ChunkPos(val x: Int = 0, val z: Int = 0, val y: Int = 0) {
    fun getChunkPosition(worldPosition: Position) = Position(worldPosition.x - x * 16, worldPosition.y - y * 16, worldPosition.z - z * 16)

    companion object fun fromPosition(position: Position) = ChunkPos((position.x / 16).roundToInt(), (position.z / 16).roundToInt(), (position.y / 16).roundToInt())
}