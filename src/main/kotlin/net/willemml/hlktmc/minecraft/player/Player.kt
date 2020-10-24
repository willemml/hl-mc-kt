package net.willemml.hlktmc.minecraft.player

import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode
import net.willemml.hlktmc.minecraft.world.types.ChunkPos
import java.util.*

data class Player(val name: String,
                  val uuid: UUID,
                  var entityID: Int = 0,
                  var onGround: Boolean = true,
                  var position: Position = Position(),
                  var chunk: ChunkPos = ChunkPos(),
                  var rotation: Rotation = Rotation(),
                  var spawnPoint: Position = Position(),
                  var health: Health = Health(),
                  var gameMode: GameMode = GameMode.SPECTATOR
)