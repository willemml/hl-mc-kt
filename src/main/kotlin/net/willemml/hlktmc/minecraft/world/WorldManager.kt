package net.willemml.hlktmc.minecraft.world

import com.github.steveice10.mc.protocol.data.game.chunk.Column
import net.willemml.hlktmc.minecraft.AIR_BLOCK
import net.willemml.hlktmc.minecraft.objects.BoundingBox
import net.willemml.hlktmc.minecraft.ResourceManager
import net.willemml.hlktmc.minecraft.objects.Block
import net.willemml.hlktmc.minecraft.world.types.BlockPos
import net.willemml.hlktmc.minecraft.world.types.ChunkPos

class WorldManager {
    val columns = HashMap<ChunkPos, Column>()

    private fun getChunk(position: ChunkPos) = columns[position.copy(y = 0)]?.chunks?.get(position.y)

    fun getBlockId(position: BlockPos) = getChunk(position.chunkPos())?.get(position.x, position.y, position.z) ?: 0

    fun getBlock(position: BlockPos) = ResourceManager.blocks[getBlockId(position)]?: AIR_BLOCK

    fun isSolid(position: BlockPos): Boolean {
        return try {
            getBlock(position).boundingBox == BoundingBox.block
        } catch (_: IndexOutOfBoundsException) {
            true
        }
    }

    private fun chunkInSquare(chunk: ChunkPos, center: ChunkPos, radius: Int) =
        chunk.x > center.x + radius ||
                chunk.x < center.x - radius ||
                chunk.z < center.z - radius ||
                chunk.z > center.z + radius

    fun addColumn(column: Column, center: ChunkPos, radius: Int) {
        val pos = ChunkPos(column.x, column.z)
        if (chunkInSquare(pos, center, radius)) columns[pos] = column
    }

    fun pruneColumns(keepingCenter: ChunkPos, keepingRadius: Int) {
        for (chunk in columns.keys) {
            if (chunkInSquare(chunk, keepingCenter, keepingRadius)) columns.remove(chunk)
        }
    }
}